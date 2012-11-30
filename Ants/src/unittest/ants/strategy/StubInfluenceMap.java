package unittest.ants.strategy;

import java.util.HashMap;
import java.util.Map;

import api.entities.Tile;
import api.map.UnitMap;
import api.pathfinder.SearchTarget;
import api.strategy.InfluenceMap;

public class StubInfluenceMap implements InfluenceMap {
    private Map<Integer, Integer> influencePerPlayer = new HashMap<Integer, Integer>();
    private Integer totalOppInfluence;

    @Override
    public int getSafety(Tile tile) {
        // TODO Auto-generated method stub
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
    public void update(UnitMap map) {
        // TODO Auto-generated method stub

    }

    public void setTotalInfluence(Integer player, Integer influence) {
        influencePerPlayer.put(player, influence);
    }

    public void setTotalOpponentInfluence(Integer influence) {
        this.totalOppInfluence = influence;
    }

    @Override
    public int getPathCosts(SearchTarget dest) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInfluence(Integer player, Tile tile) {
        // TODO Auto-generated method stub
        return 0;
    }

}
