package ants.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<Tile> bestPath(Integer strategy, Tile from, Tile to) {
        return searchStrategies.get(strategy).bestPath(from, to);
    }
}
