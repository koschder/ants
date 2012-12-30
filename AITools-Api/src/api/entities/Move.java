package api.entities;

/**
 * a move stores the current position and the direction where to move.
 * 
 * @author kases1, kustl1
 * 
 */
public class Move {
    private Tile tile;
    private Aim direction;

    public Move(Tile tile, Aim direction) {
        if (tile == null)
            throw new IllegalArgumentException("tile must be set");
        this.tile = tile;
        this.direction = direction;
    }

    public Tile getTile() {
        return tile;
    }

    public Aim getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return tile.toString() + " -->" + direction;
    }
}
