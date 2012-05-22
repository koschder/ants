package ants.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ants.entities.SearchTarget;
import ants.entities.Tile;

public abstract class PathFinder {
    public static final Integer A_STAR = 1;
    public static final Integer SIMPLE = 2;

    private static Map<Integer, SearchStrategy> searchStrategies;

    static {
        searchStrategies = new HashMap<Integer, SearchStrategy>();
        searchStrategies.put(A_STAR, new AStarSearchStrategy(6));
        searchStrategies.put(SIMPLE, new SimpleSearchStrategy());
    }

    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to) {
        return searchStrategies.get(strategy).bestPath(from, to);
    }

    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, int maxCost) {
        searchStrategies.get(strategy).setMaxCost(maxCost);
        return searchStrategies.get(strategy).bestPath(from, to);
    }

    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, Tile searchspace0,
            Tile searchspace1, int maxCost) {
        searchStrategies.get(strategy).setMaxCost(maxCost);
        searchStrategies.get(strategy).setSearchSpace(searchspace0, searchspace1);
        return searchStrategies.get(strategy).bestPath(from, to);
    }
}
