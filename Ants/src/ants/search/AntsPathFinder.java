package ants.search;

import java.util.List;

import pathfinder.PathFinder;
import ants.state.Ants;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

/***
 * stores all searchstrategies. best path can be calculated with this class.
 * 
 * @author kaeserst
 * 
 */
public class AntsPathFinder {
    public static final Integer A_STAR = 1;
    public static final Integer SIMPLE = 2;
    public static final Integer HPA_STAR = 3;

    private PathFinder pathfinder;

    public AntsPathFinder() {
        pathfinder = new PathFinder(Ants.getWorld());
    }

    /***
     * searches the best path with the defined search strategy
     * 
     * @param strategy
     *            path finding algorithm
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to) {
        return bestPath(strategy, from, to, -1);
    }

    /***
     * searches the best path with the defined search strategy. the maximum cost of the path can be defined.
     * 
     * @param strategy
     * @param from
     * @param to
     * @param maxCost
     * @return the path in a list or null if no path is found.
     */
    public List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, int maxCost) {
        if (strategy == A_STAR) {
            return pathfinder.search(PathFinder.Strategy.AStar, from, to, maxCost);
        } else if (strategy == SIMPLE) {
            return pathfinder.search(PathFinder.Strategy.Simple, from, to, maxCost);
        } else if (strategy == HPA_STAR) {
            return pathfinder.search(PathFinder.Strategy.HpaStar, from, to, maxCost);
        }
        throw new RuntimeException("unknown search strategy: " + strategy);
    }

    public void cluster() {
        pathfinder.cluster();
    }

    /***
     * searches the best path with the defined search strategy. the maximum cost of the path can be defined. A
     * restricted search area can be defined where the path must be layed in.
     * 
     * @param strategy
     * @param from
     * @param to
     * @param searchspace0
     * @param searchspace1
     * @param maxCost
     * @return the path in a list or null if no path is found.
     */
    // public static List<Tile> bestPath(Integer strategy, SearchTarget from, SearchTarget to, Tile searchspace0,
    // Tile searchspace1, int maxCost) {
    // searchStrategies.get(strategy).setMaxCost(maxCost);
    // searchStrategies.get(strategy).setSearchSpace(searchspace0, searchspace1);
    // return searchStrategies.get(strategy).search(from, to);
    // }
}
