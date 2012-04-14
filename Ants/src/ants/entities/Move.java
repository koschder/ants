package ants.entities;

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
