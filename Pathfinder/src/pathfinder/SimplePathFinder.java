package pathfinder;

import java.util.List;

import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;
import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableMap;

public class SimplePathFinder implements PathFinder {

    protected SearchableMap map;

    public SimplePathFinder(SearchableMap map) {
        this.map = map;
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, int maxCost) {
        SearchStrategy searchStrat = getStrategy(strategy);
        searchStrat.setMaxCost(maxCost);
        return searchStrat.search(start, end);
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end) {
        return search(strategy, start, end, -1);
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, Tile searchSpace0,
            Tile searchSpace1, int maxCost) {
        SearchStrategy searchStrat = getStrategy(strategy);
        searchStrat.setMaxCost(maxCost);
        searchStrat.setSearchSpace(searchSpace0, searchSpace1);
        return searchStrat.search(start, end);
    }

    /***
     * 
     * @return the map on witch the path finder acts.
     */
    @Override
    public SearchableMap getMap() {
        return this.map;
    }

    /***
     * 
     * @param strategy
     * @return the instance of the search strategy
     */
    protected SearchStrategy getStrategy(Strategy strategy) {
        if (strategy == Strategy.Simple)
            return new SimpleSearchStrategy(this);
        if (strategy == Strategy.AStar)
            return new AStarSearchStrategy(this);

        throw new RuntimeException("Strategy not implemented: " + strategy);
    }

    @Override
    public void update() {
        // nothing to do
    }

}