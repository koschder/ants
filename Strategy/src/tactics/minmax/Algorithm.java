package tactics.minmax;

/**
 * Interface for an algorithm implementation for finding the best move for a given {@link Game}.
 * 
 * @author kases1, kustl1
 * 
 */
public interface Algorithm {

    /**
     * Calculate the best move, limiting the search depth by maxDepth.
     * 
     * @param g
     * @param maxDepth
     * @return
     */
    public Game bestMove(Game g, int maxDepth);

    /**
     * Calculate the best move, limiting the search by the given time limit.
     * 
     * @param g
     * @param timeLimit
     * @return
     */
    public Game bestMoveTimeLimited(Game g, int timeLimit);
}
