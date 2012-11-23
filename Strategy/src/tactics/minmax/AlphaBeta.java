package tactics.minmax;

import java.util.Iterator;

/**
 * Implementation of Min-Max Algorithm with Alpha-Beta pruning.
 * 
 * @author kustl1
 * @author kases1
 * 
 */
public class AlphaBeta extends AbstractAlgorithm {

    long _alpha = Long.MIN_VALUE;
    long _beta = Long.MAX_VALUE;

    @Override
    public long eval(Game g, int maxDepth) {
        return eval(g, _alpha, _beta, maxDepth);
    }

    private long eval(Game g, long alpha, long beta, int maxDepth) {
        // System.out.println(String.format("eval(alpha %s, beta %s, depth %s)", alpha, beta, maxDepth));
        if (maxDepth == 0) {
            int hVal = g.evalHeuristicValue();
            // System.out.println("Heuristic Value: " + hVal);
            return hVal;
        }
        if (g.isTerminal())
            return g.getUtilityValue();

        if (g.isMaxToMove()) {
            for (Iterator<Game> successors = g.expand(); successors.hasNext();) {
                Game game = successors.next();
                alpha = Math.max(alpha, eval(game, alpha, beta, maxDepth - 1));
                if (alpha >= beta)
                    return beta; // beta-cutoff
            }
            return alpha;
        } else {
            for (Iterator<Game> successors = g.expand(); successors.hasNext();) {
                Game game = successors.next();
                beta = Math.min(beta, eval(game, alpha, beta, maxDepth - 1));
                if (alpha >= beta)
                    return alpha; // alpha-cutoff
            }
            return beta;
        }
    }

}
