package tactics.minmax;

import java.util.Iterator;
import java.util.List;

import api.entities.Move;

/**
 * 
 * @author kases1, kustl1
 * @deprecated Did not work as hoped, abandoned.
 * 
 */
public interface Game {

    /**
     * @param move
     * @return the next game state after performing the move.
     */
    public Game move(List<Move> move);

    /**
     * @return the move that lead to this game state.
     */
    public List<Move> getMoves();

    /**
     * @return an iterator over the successor game states.
     */
    public Iterator<Game> expand();

    /**
     * @return the heuristic evaluation of this game state.
     */
    public int evalHeuristicValue();

    /**
     * @return the utility value of this game state. Must only be called if isTerminal() returns true.
     */
    public int getUtilityValue();

    /**
     * @return true if this is a terminal game state (i.e. a player has won)
     */
    public boolean isTerminal();

    /**
     * @return true if it is the MAX player's turn
     */
    public boolean isMaxToMove();

    public abstract boolean isValid();
}
