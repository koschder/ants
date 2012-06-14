package ants.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ants.entities.SearchTarget;
import ants.entities.Tile;

/***
 * stores all searchstrategies. best path can be calculated with this class.
 * @author kaeserst
 *
 */
public abstract class PathFinder {
    public static final Integer A_STAR = 1;
    public static final Integer SIMPLE = 2;
    public static final Integer HPA_STAR = 3;

    private static Map<Integer, SearchStrategy> searchStrategies;

    static {
        searchStrategies = new HashMap<Integer, SearchStrategy>();
        searchStrategies.put(A_STAR, new AStarSearchStrategy(6));
        searchStrategies.put(SIMPLE, new SimpleSearchStrategy());
        searchStrategies.put(HPA_STAR, new HPAStarSearchStrategy());
    }

/***
 * searches the best path with the defined search strategy
 * @param strategy path finding algorithm
 * @param from
 * @param to
 * @return the path in a list or null if no path is found.
 */
    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to) {
        return searchStrategies.get(strategy).bestPath(from, to);
    }

    /***
     * searches the best path with the defined search strategy. the maximum cost of the path can be defined.
     * @param strategy
     * @param from
     * @param to
     * @param maxCost
     * @return the path in a list or null if no path is found.
     */
    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, int maxCost) {
        searchStrategies.get(strategy).setMaxCost(maxCost);
        searchStrategies.get(strategy).setSearchSpace(null, null);
        return searchStrategies.get(strategy).bestPath(from, to);
    }

    /***
     * searches the best path with the defined search strategy. the maximum cost of the path can be defined.
     * A restricted search area can be defined where the path must be layed in.
     * @param strategy
     * @param from
     * @param to
     * @param searchspace0
     * @param searchspace1
     * @param maxCost 
     * @return the path in a list or null if no path is found.
     */
    public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, Tile searchspace0,
            Tile searchspace1, int maxCost) {
        searchStrategies.get(strategy).setMaxCost(maxCost);
        searchStrategies.get(strategy).setSearchSpace(searchspace0, searchspace1);
        return searchStrategies.get(strategy).bestPath(from, to);
    }
}
