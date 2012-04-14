package ants.entities;

/**
 * Represents type of tile on the game map.
 */
public enum Ilk {
    /** Water tile. */
    WATER,

    /** Food tile. */
    FOOD,

    /** Land tile. */
    LAND,

    /** Dead ant tile. */
    DEAD,

    /** My ant tile. */
    MY_ANT,

    /** Enemy ant tile. */
    ENEMY_ANT;

    /**
     * Checks if this type of tile is passable, which means it is not a water tile.
     * 
     * @return <code>true</code> if this is not a water tile, <code>false</code> otherwise
     */
    public boolean isPassable() {
        return ordinal() > WATER.ordinal();
    }

    /**
     * Checks if this type of tile is unoccupied, which means it is a land tile or a dead ant tile.
     * 
     * @return <code>true</code> if this is a land tile or a dead ant tile, <code>false</code> otherwise
     */
    public boolean isUnoccupied() {
        return this == LAND || this == DEAD;
    }

    public boolean isFood() {
        return this == FOOD;
    }

    public boolean hasFriendlyAnt() {
        // TODO Auto-generated method stub
        return this == MY_ANT;
    }
}
