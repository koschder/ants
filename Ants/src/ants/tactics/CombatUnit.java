package ants.tactics;

import api.entities.*;

public class CombatUnit implements Unit {

    private int player;
    private Tile tile;

    public CombatUnit(Unit unit) {
        this.player = unit.getPlayer();
        this.tile = unit.getTile();
    }

    @Override
    public boolean isMine() {
        return this.player == 0;
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

}
