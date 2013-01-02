package unittest.ants.strategy;

import java.util.HashMap;
import java.util.Map;

import api.entities.Tile;
import api.search.SearchTarget;
import api.search.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class StubInfluenceMap implements InfluenceMap {
    private Map<Integer, Integer> influencePerPlayer = new HashMap<Integer, Integer>();
    private Integer totalOppInfluence;

    @Override
    public int getSafety(Tile tile) {
        return 0;
    }

    @Override
    public int getTotalInfluence(Integer player) {
        return influencePerPlayer.get(player);
    }

    @Override
    public int getTotalOpponentInfluence() {
        return totalOppInfluence;
    }

    @Override
    public void update(SearchableUnitMap map) {

    }

    public void setTotalInfluence(Integer player, Integer influence) {
        influencePerPlayer.put(player, influence);
    }

    public void setTotalOpponentInfluence(Integer influence) {
        this.totalOppInfluence = influence;
    }

    @Override
    public int getPathCosts(SearchTarget dest) {
        return 0;
    }

    @Override
    public int getInfluence(Integer player, Tile tile) {
        return 0;
    }

}
