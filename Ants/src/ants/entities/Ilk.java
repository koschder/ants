package ants.entities;

/**
 * Represents type of tile on the game map.
 * 
 * @author kases1, kustl1
 * @author adapted from the starter package from aichallenge.org
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

    /**
     * 
     * @return true if it is a food Ilk.
     */
    public boolean isFood() {
        return this == FOOD;
    }

    /**
     * 
     * @return true if there is a friendly ant.
     */
    public boolean hasFriendlyAnt() {
        return this == MY_ANT;
    }

    /**
     * 
     * @return true if there is a friendly ant.
     */
    public boolean hasEnemyAnt() {
        return this == ENEMY_ANT;
    }

    @Override
    public String toString() {
        switch (this) {
        case DEAD:
            return "+";
        case ENEMY_ANT:
            return "E";
        case MY_ANT:
            return "A";
        case FOOD:
            return "f";
        case LAND:
            return ".";
        case WATER:
            return "W";
        default:
            return "";
        }
    }
}
