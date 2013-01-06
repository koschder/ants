package api.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import api.search.PathPiece;

/**
 * Represents a tile of the game map.
 * 
 * @author kases1, kustl1
 * @author adapted from the starter package from aichallenge.org
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

    @Override
    public int compareTo(Tile o) {
        return hashCode() - o.hashCode();
    }

    /**
     * This Class compares two tiles according to their distance from a given third tile.
     * 
     * @author kases1, kustl1
     * 
     */
    public static class DistanceComparator implements Comparator<Tile> {

        Map<Tile, Integer> base;

        /**
         * Default Constructor for {@link DistanceComparator}
         * 
         * @param base
         *            a map of Tiles and their distances from the reference Tile
         */
        public DistanceComparator(Map<Tile, Integer> base) {
            this.base = base;
        }

        public int compare(Tile a, Tile b) {
            return base.get(a).compareTo(base.get(b));
        }
    }

    @Override
    public int hashCode() {
        return row * 2000 + col;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            Tile tile = (Tile) o;
            result = (row == tile.row && col == tile.col);
        }
        return result;
    }

    @Override
    public String toString() {
        return "<r:" + row + " c:" + col + ">";
    }

    @Override
    public List<Tile> getPath() {
        return new ArrayList<Tile>(Arrays.asList(this));
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