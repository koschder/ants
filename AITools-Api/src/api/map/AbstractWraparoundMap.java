package api.map;

import java.util.ArrayList;
import java.util.List;

import api.entities.Aim;
import api.entities.Tile;
import api.pathfinder.SearchableMap;

public abstract class AbstractWraparoundMap implements SearchableMap {

    @Override
    public WorldType getWorldType() {
        return WorldType.Globe;
    }

    @Override
    public List<Aim> getDirections(Tile t1, Tile t2) {
        List<Aim> directions = new ArrayList<Aim>();
        if (t1.getRow() < t2.getRow()) {
            if (t2.getRow() - t1.getRow() >= getRows() / 2) {
                directions.add(Aim.NORTH);
            } else {
                directions.add(Aim.SOUTH);
            }
        } else if (t1.getRow() > t2.getRow()) {
            if (t1.getRow() - t2.getRow() >= getRows() / 2) {
                directions.add(Aim.SOUTH);
            } else {
                directions.add(Aim.NORTH);
            }
        }
        if (t1.getCol() < t2.getCol()) {
            if (t2.getCol() - t1.getCol() >= getCols() / 2) {
                directions.add(Aim.WEST);
            } else {
                directions.add(Aim.EAST);
            }
        } else if (t1.getCol() > t2.getCol()) {
            if (t1.getCol() - t2.getCol() >= getCols() / 2) {
                directions.add(Aim.EAST);
            } else {
                directions.add(Aim.WEST);
            }
        }
        return directions;
    }

    @Override
    public Tile getTile(Tile tile, Aim direction) {
        if (direction == null)
            return tile;
        int row = (tile.getRow() + direction.getRowDelta()) % getRows();
        if (row < 0) {
            row += getRows();
        }
        int col = (tile.getCol() + direction.getColDelta()) % getCols();
        if (col < 0) {
            col += getCols();
        }
        return new Tile(row, col);
    }

    /**
     * Returns location with the specified offset from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param offset
     *            offset to look up
     * 
     * @return location with <code>offset</code> from <cod>tile</code>
     */
    @Override
    public Tile getTile(Tile tile, Tile offset) {
        int row = (tile.getRow() + offset.getRow()) % getRows();
        if (row < 0) {
            row += getRows();
        }
        int col = (tile.getCol() + offset.getCol()) % getCols();
        if (col < 0) {
            col += getCols();
        }
        return new Tile(row, col);
    }

    @Override
    public int manhattanDistance(Tile tStart, Tile tEnd) {

        int c = 0;
        int r = 0;
        c = Math.abs(tStart.getCol() - tEnd.getCol());
        r = Math.abs(tStart.getRow() - tEnd.getRow());

        // considering warparound
        if (c > getCols() / 2)
            c = getCols() - c;

        if (r > getRows() / 2)
            r = getRows() - r;

        return r + c;
    }

    public List<Tile> get4Neighbours(Tile center) {
        List<Tile> neighbours = new ArrayList<Tile>();
        for (Aim aim : Aim.values()) {
            neighbours.add(getTile(center, aim));
        }
        return neighbours;
    }

    @Override
    public double beelineTo(Tile tStart, Tile tEnd) {

        int c = 0;
        int r = 0;
        c = Math.abs(tStart.getCol() - tEnd.getCol());
        r = Math.abs(tStart.getRow() - tEnd.getRow());

        // considering warparound
        if (c > getCols() / 2)
            c = getCols() - c;

        if (r > getRows() / 2)
            r = getRows() - r;

        return Math.sqrt(r * r + c * c);
    }

}