package api.map;

import java.util.List;
import java.util.Set;

import api.entities.Unit;

/**
 * The UnitMap interface defines methods to retrieve the units positioned on a {@link TileMap}
 * 
 * @author kases1, kustl1
 * 
 */
public interface UnitMap extends TileMap {
    /**
     * 
     * @param player
     * @return the units owned by the given player
     */
    public List<Unit> getUnits(int player);

    /**
     * 
     * @return a set of all players with units on the map
     */
    public Set<Integer> getPlayers();
}
