package api.map;

import java.util.ArrayList;
import java.util.List;

import api.entities.Aim;
import api.entities.Tile;
import api.pathfinder.SearchableMap;

public abstract class AbstractWraparoundMap implements SearchableMap {

    protected int rows;
    protected int cols;

    public AbstractWraparoundMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

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

    public List<Tile> get8Neighbours(Tile center) {
        List<Tile> neighbours = new ArrayList<Tile>();
        for (Aim aim : Aim.values()) {
            neighbours.add(getTile(center, aim));
        }
        neighbours.add(getTile(center, new Tile(1, 1)));
        neighbours.add(getTile(center, new Tile(-1, -1)));
        neighbours.add(getTile(center, new Tile(1, -1)));
        neighbours.add(getTile(center, new Tile(-1, 1)));

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

    /**
     * Calculates distance between two locations on the game map.
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    @Override
    public int getSquaredDistance(Tile t1, Tile t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }

    @Override
    public Tile getClusterCenter(List<Tile> cluster) {

        int row = getAvgRowDistanceFromOrigin(cluster);
        int col = getAvgColDistanceFromOrigin(cluster);

        return getTile(new Tile(0, 0), new Tile(row, col));
    }

    private int getAvgColDistanceFromOrigin(List<Tile> cluster) {
        int sum = 0;
        int count;
        for (count = 0; count < cluster.size(); count++) {
            sum += getColDistanceFromOrigin(cluster.get(count));
        }
        return sum / count;
    }

    private int getAvgRowDistanceFromOrigin(List<Tile> cluster) {
        int sum = 0;
        int count;
        for (count = 0; count < cluster.size(); count++) {
            sum += getRowDistanceFromOrigin(cluster.get(count));
        }
        return sum / count;
    }

    private int getRowDistanceFromOrigin(Tile tile) {
        return tile.getRow() > (rows / 2) ? tile.getRow() - rows : tile.getRow();
    }

    private int getColDistanceFromOrigin(Tile tile) {
        return tile.getCol() > (cols / 2) ? tile.getCol() - cols : tile.getCol();
    }

}