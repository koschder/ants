package api.entities;

/**
 * A Move stores the current position and the direction where to move.
 * 
 * @author kases1, kustl1
 * @author adapted from the starter package from aichallenge.org
 */
public class Move {
    private Tile tile;
    private Aim direction;

    /**
     * Default constructor
     * 
     * @param tile
     * @param direction
     */
    public Move(Tile tile, Aim direction) {
        if (tile == null)
            throw new IllegalArgumentException("tile must be set");
        this.tile = tile;
        this.direction = direction;
    }

    /**
     * 
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * 
     * @return the direction
     */
    public Aim getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return tile.toString() + " -->" + direction;
    }
}
