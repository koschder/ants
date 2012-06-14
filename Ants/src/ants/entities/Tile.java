package ants.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ants.state.Ants;

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
        return row * Ants.MAX_MAP_SIZE + col;
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

    @Override
    public List<SearchTarget> getSuccessors() {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        list.add(Ants.getWorld().getTile(this, Aim.NORTH));
        list.add(Ants.getWorld().getTile(this, Aim.SOUTH));
        list.add(Ants.getWorld().getTile(this, Aim.WEST));
        list.add(Ants.getWorld().getTile(this, Aim.EAST));
        return list;
    }

    @Override
    public boolean isSearchable(boolean bParentNode) {

        return Ants.getWorld().getIlk(this).isPassable() && !isOccupiedForNextMove(bParentNode);
    }

    private boolean isOccupiedForNextMove(boolean bParentNode) {
        if (!bParentNode) // we are on the 2nd level of the search tree
            return Ants.getOrders().getOrders().containsValue(this);
        return false;
    }

   

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

    @Override
    public int manhattanDistanceTo(SearchTarget dest) {
            if (dest instanceof Tile)
                return manhattanDistanceTo((Tile) dest);

            throw new RuntimeException("distanceTo for a Tile to a " + dest.getClass() + "not implemented");
    }

    @Override
    public double beelineTo(SearchTarget searchTarget) {
        Tile dest = searchTarget.getTargetTile();
        return Math.sqrt(Math.pow(Math.abs(dest.col - this.col)-0.01,2) + Math.pow(Math.abs(dest.row - this.row),2))-0.01;
    }

}
