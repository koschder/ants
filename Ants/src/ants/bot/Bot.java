package ants.bot;

import ants.entities.Ilk;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate() {
        Ants.getAnts().nextTurn();
        Ants.getAnts().clearState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWater(int row, int col) {
        Ants.getAnts().update(Ilk.WATER, new Tile(row, col));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnt(int row, int col, int owner) {
        Ants.getAnts().update(owner > 0 ? Ilk.ENEMY_ANT : Ilk.MY_ANT, new Tile(row, col), owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFood(int row, int col) {
        Ants.getAnts().update(Ilk.FOOD, new Tile(row, col));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnt(int row, int col, int owner) {
        Ants.getAnts().update(Ilk.DEAD, new Tile(row, col));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHill(int row, int col, int owner) {
        Ants.getWorld().addHill(owner, new Tile(row, col));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate() {
        Ants.getAnts().setVision();
        Ants.getWorld().updateHills();
    }
}
