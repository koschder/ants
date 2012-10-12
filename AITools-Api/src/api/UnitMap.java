package api;

import java.util.Collection;
import java.util.Set;

public interface UnitMap extends TileMap {
    public Collection<Unit> getUnits(int player);

    public Set<Integer> getPlayers();
}
