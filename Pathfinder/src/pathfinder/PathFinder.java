package pathfinder;

import java.util.List;

import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableMap;
import api.strategy.InfluenceMap;

public interface PathFinder {

    public enum Strategy {
        Simple,
        AStar,
        HpaStar
    };

    // called every turn, updates clustering
    public void update();

    /***
     * 
     * @param strategy
     *            witch strategy should be applied
     * @param start
     *            position
     * @param end
     *            position
     * @param maxCost
     *            maximum Costs for the path, if param is set to -1 no costs are calculated.
     * @return the found path, or null if no path found.
     */
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, int maxCost);

    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end);

    /***
     * 
     * @param strategy
     *            witch strategy should be applied
     * @param start
     *            position
     * @param end
     *            position
     * @param searchSpace0
     *            the left top corner of the search area in witch the framework can search the path
     * @param searchSpace1
     *            the right bottom corner of the search area in witch the framework can search the path
     * @param maxCost
     *            maximum Costs for the path, if param is set to -1 no costs are calculated.
     * @return the found path, or null if no path found.
     */
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, Tile searchSpace0,
            Tile searchSpace1, int maxCost);

    public SearchableMap getMap();

    public InfluenceMap getInfluenceMap();

}