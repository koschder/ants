package api.map;

import java.util.List;
import java.util.Set;

import api.entities.Unit;

/**
 * the interface of the UnitMap, unit map defines a Unit of a player positioned on the map
 * 
 * @author kases1, kustl1
 * 
 */
public interface UnitMap extends TileMap {
    public List<Unit> getUnits(int player);

    public Set<Integer> getPlayers();
}
