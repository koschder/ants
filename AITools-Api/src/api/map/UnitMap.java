package api.map;

import java.util.List;
import java.util.Set;

import api.entities.Unit;

public interface UnitMap extends TileMap {
    public List<Unit> getUnits(int player);

    public Set<Integer> getPlayers();
}
