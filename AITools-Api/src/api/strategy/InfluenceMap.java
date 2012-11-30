package api.strategy;

import api.entities.Tile;
import api.map.UnitMap;
import api.pathfinder.SearchTarget;

public interface InfluenceMap {
    public int getSafety(Tile tile);

    public int getTotalInfluence(Integer player);

    public int getTotalOpponentInfluence();

    public abstract void update(UnitMap map);

    public int getPathCosts(SearchTarget dest);

    public int getInfluence(Integer player, Tile tile);
}
