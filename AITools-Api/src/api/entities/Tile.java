package api.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import api.search.PathPiece;

/**
 * Represents a tile of the game map.
 */
public class Tile implements Comparable<Tile>, PathPiece {
    private final int row;

    private final int col;

    /**
     * Creates new {@link Tile} object.
     * 
     * @param row
     *            row index
     * @param col
     *            column index
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns row index.
     * 
     * @return row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns column index.
     * 
     * @return column index
     */
    public int getCol() {
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Tile o) {
        return hashCode() - o.hashCode();
    }

    public static class DistanceComparator implements Comparator<Tile> {

        Map<Tile, Integer> base;

        public DistanceComparator(Map<Tile, Integer> base) {
            this.base = base;
        }

        public int compare(Tile a, Tile b) {
            return base.get(a).compareTo(base.get(b));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return row * 2000 + col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            Tile tile = (Tile) o;
            result = (row == tile.row && col == tile.col);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "<r:" + row + " c:" + col + ">";
    }

    @Override
    public List<Tile> getPath() {
        return new ArrayList<Tile>(Arrays.asList(this));
    }

    @Override
    public boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2) {

        if (searchSpace1 == null || searchSpace2 == null)
            return true; // no searchspace defined.

        if (searchSpace1.getRow() <= this.getRow() && searchSpace1.getCol() <= this.getCol())
            if (searchSpace2.getRow() >= this.getRow() && searchSpace2.getCol() >= this.getCol())
                return true;

        return false;
    }

    @Override
    public Tile getTargetTile() {
        return this;

    }

    @Override
    public String toShortString() {
        return toString();
    }

    @Override
    public boolean isFinal(PathPiece to) {
        return this.equals(to);
    }

    @Override
    public int getCost() {
        // one move cost 1
        return 1;
    }

}
