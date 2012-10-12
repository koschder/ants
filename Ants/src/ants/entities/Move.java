package ants.entities;

import api.Aim;
import api.Tile;

/***
 * a move stores the current position and the direction where to move.
 * @author kustl1, kases1
 *
 */
public class Move {
    private Tile tile;
    private Aim direction;

    public Move(Tile tile, Aim direction) {
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
