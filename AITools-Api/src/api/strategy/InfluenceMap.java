package api.strategy;

import api.entities.Tile;
import api.map.UnitMap;

public interface InfluenceMap {
    public int getSafety(Tile tile);

    public int getTotalInfluence(Integer player);

    public int getTotalOpponentInfluence();

    public abstract void update(UnitMap map);
}
