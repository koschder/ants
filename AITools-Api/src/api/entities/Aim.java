package api.entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a direction in which to move a unit. The directions are NORTH,EAST,SOUTH and WEST.
 * 
 * @author kases
 * @author adapted from the starter package from aichallenge.org
 */
public enum Aim {
    /** North direction, or up. */
    NORTH(-1, 0, 'n'),

    /** East direction or right. */
    EAST(0, 1, 'e'),

    /** South direction or down. */
    SOUTH(1, 0, 's'),

    /** West direction or left. */
    WEST(0, -1, 'w');

    private static final Map<Character, Aim> symbolLookup = new HashMap<Character, Aim>();

    static {
        symbolLookup.put('n', NORTH);
        symbolLookup.put('e', EAST);
        symbolLookup.put('s', SOUTH);
        symbolLookup.put('w', WEST);
    }

    private final int rowDelta;

    private final int colDelta;

    private final char symbol;

    Aim(int rowDelta, int colDelta, char symbol) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.symbol = symbol;
    }

    /**
     * Returns rows delta.
     * 
     * @return rows delta.
     */
    public int getRowDelta() {
        return rowDelta;
    }

    /**
     * Returns columns delta.
     * 
     * @return columns delta.
     */
    public int getColDelta() {
        return colDelta;
    }

    /**
     * Returns symbol associated with this direction.
     * 
     * @return symbol associated with this direction.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns direction associated with specified symbol.
     * 
     * @param symbol
     *            <code>n</code>, <code>e</code>, <code>s</code> or <code>w</code> character
     * 
     * @return direction associated with specified symbol
     */
    public static Aim fromSymbol(char symbol) {
        return symbolLookup.get(symbol);
    }

    /**
     * Returns a list of aims that are orthogonal to the given aim.
     * 
     * @param aim
     * @return a list of orthogonal aims
     */
    public static List<Aim> getOrthogonalAims(Aim aim) {
        switch (aim) {
        case NORTH:
        case SOUTH:
            return Arrays.asList(WEST, EAST);
        default:
            return Arrays.asList(NORTH, SOUTH);
        }
    }

    /**
     * Returns the opposite aim for the given aim.
     * 
     * @param aim
     * @return the opposite aim
     */
    public static Aim getOpposite(Aim aim) {
        switch (aim) {
        case NORTH:
            return SOUTH;
        case SOUTH:
            return NORTH;
        case EAST:
            return WEST;
        case WEST:
            return EAST;
        }
        return null;
    }
}
