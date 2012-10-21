package api.entities;

import java.util.Arrays;
import java.util.List;

import api.pathfinder.SearchTarget;

/**
 * Represents a tile of the game map.
 */
public class Tile implements Comparable<Tile>, SearchTarget {
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

    /***
     * 
     * @param dest
     * @return the manhattanDistanceTo an ohter tile
     */
    public int manhattanDistanceTo(Tile dest) {
        return Math.abs(dest.col - this.col) + Math.abs(dest.row - this.row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Tile o) {
        return hashCode() - o.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO unschen
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

    public Aim directionTo(Tile nextStep) {
        if (getRow() == nextStep.getRow()) {
            // changing in column east or west
            int diff = getCol() - nextStep.getCol();
            if (diff == getCol() || (diff < 0 && diff != -getCol())) {
                return Aim.EAST;
            } else {
                return Aim.WEST;
            }
        } else {
            // changing in row north or south
            int diff = getRow() - nextStep.getRow();
            if (diff == getRow() || (diff < 0 && diff != -getRow())) {
                return Aim.SOUTH;
            } else {
                return Aim.NORTH;
            }
        }
    }

    // @Override
    // public boolean isSearchable(boolean bParentNode) {
    // return Ants.getWorld().getIlk(this).isPassable() && !isOccupiedForNextMove(bParentNode);
    // }

    @Override
    public List<Tile> getPath() {
        return Arrays.asList(this);
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
    public boolean isFinal(SearchTarget to) {
        return this.equals(to);
    }

    @Override
    public int getCost() {
        // one move cost 1
        return 1;
    }

}
