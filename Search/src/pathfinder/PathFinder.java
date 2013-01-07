package pathfinder;

import java.util.List;

import api.entities.Tile;
import api.search.PathPiece;
import api.search.SearchableMap;
import api.strategy.InfluenceMap;

/**
 * The interface for path-finding algorithms, all needed methods for path finding are described.
 * 
 * @author kases1, kustl1
 * 
 */
public interface PathFinder {

    /**
     * the implemented Strategies are Simple, AStar, HPAStar
     * 
     */
    public enum Strategy {
        Simple,
        AStar,
        HpaStar
    };

    /**
     * called every turn to do updates on the path finder if needed.
     */
    public void update();

    /**
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
    public List<Tile> search(Strategy strategy, PathPiece start, PathPiece end, int maxCost);

    /**
     * 
     * @param strategy
     *            witch strategy should be applied
     * @param start
     *            position
     * @param end
     *            position
     * @return the found path, or null if no path found.
     */
    public List<Tile> search(Strategy strategy, PathPiece start, PathPiece end);

    /**
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
    public List<Tile> search(Strategy strategy, PathPiece start, PathPiece end, Tile searchSpace0, Tile searchSpace1,
            int maxCost);

    /**
     * 
     * @return the map where the path finder is applied to.
     */
    public SearchableMap getMap();

    /**
     * 
     * @return the influence map used on path finding (optionally)
     */
    public InfluenceMap getInfluenceMap();

}