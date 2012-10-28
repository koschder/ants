package ants.entities;

import api.entities.Aim;
import api.entities.Tile;

/***
 * a move stores the current position and the direction where to move.
 * 
 * @author kustl1, kases1
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

}
