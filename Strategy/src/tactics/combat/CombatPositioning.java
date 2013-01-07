package tactics.combat;

import api.entities.Tile;
import api.entities.Unit;

/**
 * This interface defines methods to be implemented by classes that deal with positioning units for combat situations.
 * Note that this does not define a method for calculating the positions; this should be done in the constructor.
 * 
 * @author kases1, kustl1
 * 
 */
public interface CombatPositioning {
    /**
     * Gets the tile the given unit should move to next
     * 
     * @param u
     * @return the next tile
     */
    public Tile getNextTile(Unit u);

    /**
     * Allows us to prepare a log string that can be used later
     * 
     * @return the log message
     */
    public String getLog();

}
