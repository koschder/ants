package ants.bot;

import ants.entities.Ilk;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Provides basic game state handling.
 * 
 * @author kases1, kustl1
 * @author adapted from the starter package from aichallenge.org
 */
public abstract class Bot extends AbstractSystemInputParser {

    @Override
    public void beforeUpdate() {
        Ants.getAnts().nextTurn();
        Ants.getAnts().clearState();
    }

    @Override
    public void addWater(int row, int col) {
        Ants.getAnts().update(Ilk.WATER, new Tile(row, col));
    }

    @Override
    public void addAnt(int row, int col, int owner) {
        Ants.getAnts().update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col), owner);
    }

    @Override
    public void addFood(int row, int col) {
        Ants.getAnts().update(Ilk.FOOD, new Tile(row, col));
    }

    @Override
    public void removeAnt(int row, int col, int owner) {
        Ants.getAnts().update(Ilk.DEAD, new Tile(row, col));
    }

    @Override
    public void addHill(int row, int col, int owner) {
        Ants.getWorld().addHill(owner, new Tile(row, col));
    }

    @Override
    public void afterUpdate() {
        Ants.getAnts().setVision();
        Ants.getWorld().updateHills();
    }
}
