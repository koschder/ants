package ants.entities;

import ants.state.Ants;

/**
 * Represents a tile of the game map.
 */
public class Tile implements Comparable<Tile> {
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
            result = row == tile.row && col == tile.col;
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
}