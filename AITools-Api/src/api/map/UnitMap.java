package api.map;

import java.util.Collection;
import java.util.Set;

import api.entities.Unit;

public interface UnitMap extends TileMap {
    public Collection<Unit> getUnits(int player);

    public Set<Integer> getPlayers();
}
