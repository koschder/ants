package api.strategy;

import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableUnitMap;

public interface InfluenceMap {
    public int getSafety(Tile tile);

    public int getTotalInfluence(Integer player);

    public int getTotalOpponentInfluence();

    public void update(SearchableUnitMap map);

    public int getPathCosts(SearchTarget dest);

    public int getInfluence(Integer player, Tile tile);
}
