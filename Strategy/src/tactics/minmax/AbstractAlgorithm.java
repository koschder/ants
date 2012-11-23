package tactics.minmax;

import java.util.Iterator;

/**
 * Abstract base class for an {@link Algorithm} implementation.
 * 
 * @author kustl1
 * @author kases1
 * 
 */
public abstract class AbstractAlgorithm implements Algorithm {

    @Override
    public Game bestMove(Game g, int maxDepth) {
        long bestValue = Integer.MIN_VALUE;
        Game bestState = null;
        long eval;

        for (Iterator<Game> successors = g.expand(); successors.hasNext();) {
            Game game = successors.next();
            eval = eval(game, maxDepth);

            if (eval > bestValue) {
                bestValue = eval;
                bestState = game;
            }
        }

        return bestState;
    }

    @Override
    public Game bestMoveTimeLimited(Game g, int timeLimit) {

        Iterator<Game> canMoveIteratior = g.expand();
        if (!canMoveIteratior.hasNext())
            return null; // we are blocked return null;

        final long start = System.currentTimeMillis();
        int maxDepth = 1;
        long bestValue = Integer.MIN_VALUE;
        Game bestState = null;
        long eval;
        long lastIteration = 0;
        int iterations = 0;
        while (shouldSearchDeeper(timeLimit, start, lastIteration)) {
            System.out.println("Starting new iteration with maxDepth = " + maxDepth + ": last one took "
                    + lastIteration + " ms.");
            final long iterationStart = System.currentTimeMillis();
            for (Iterator<Game> successors = g.expand(); successors.hasNext();) {
                Game game = successors.next();
                if (!game.isValid())
                    continue;
                eval = eval(game, maxDepth);
                // System.out.println("eval: " + eval);
                if (eval > bestValue) {
                    bestValue = eval;
                    bestState = game;
                }
            }
            lastIteration = System.currentTimeMillis() - iterationStart;
            iterations++;
            maxDepth++;
        }
        final long elapsedTime = System.currentTimeMillis() - start;
        if (elapsedTime > timeLimit)
            throw new RuntimeException("took too long!");
        System.out.println(String.format("Searched in %s iterations with maxDepth = %s, BestValue = %s, took %s ms",
                iterations, maxDepth, bestValue, elapsedTime));
        // if we couldn't find a bestState, just return the first move
        return bestState == null ? g.expand().next() : bestState;
    }

    private boolean shouldSearchDeeper(int timeLimit, final long start, long lastIteration) {
        final long elapsedTime = System.currentTimeMillis() - start;
        final long estimatedTime = (elapsedTime + 8 * lastIteration);
        System.out
                .println(String
                        .format("timeLimit: %s, start: %s, elapsedTime: %s, lastIteration: %s estimated time for next deeper interation: %s",
                                timeLimit, start, elapsedTime, lastIteration, estimatedTime));
        return timeLimit > estimatedTime;
    }

    /**
     * Evaluate the Game, search depth limited by maxDepth.
     * 
     * @param g
     * @param maxDepth
     * @return
     */
    protected abstract long eval(Game g, int maxDepth);
}
