package pathfinder;

import java.util.List;

import api.SearchTarget;
import api.SearchableMap;
import api.Tile;



import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.HPAStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;

/***
 * this is the main class of the pathfinder framework
 * 
 * @author kaeserst
 * 
 */
public class PathFinder {

    public enum Strategy {
        Simple,
        AStar,
        HpaStar
    };

    private SearchableMap map;
    private Clustering cluster;

    /***
     * setting the map for pathfinding
     * 
     * @param m
     *            a instance of a searchable map
     */
    public void setMap(SearchableMap m) {
        map = m;
    }

    /***
     * Initialize the clustering
     * 
     * @param clusterSize
     *            the size of each cluster
     * @param clusterType
     *            Corner for vertices on the corners of each cluster pass way, or Centered if the vertex should be
     *            centered at the passage
     * 
     */
    public void initClustering(int clusterSize, ClusterType clusterType) {

        cluster = new Clustering(this, clusterSize, map.getRows(), map.getCols());
        cluster.setClusterType(clusterType);
        cluster.setWorldType(map.getWorldType());

    }

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
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, int maxCost) {
        SearchStrategy searchStrat = getStartegy(strategy);
        searchStrat.setMaxCost(maxCost);
        return searchStrat.search(start, end);
    }

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
            Tile searchSpace1, int maxCost) {
        SearchStrategy searchStrat = getStartegy(strategy);
        searchStrat.setMaxCost(maxCost);
        searchStrat.setSearchSpace(searchSpace0, searchSpace1);
        return searchStrat.search(start, end);
    }

    /***
     * 
     * @param strategy
     * @return the instance of the search strategy
     */
    private SearchStrategy getStartegy(Strategy strategy) {
        if (strategy == Strategy.Simple)
            return new SimpleSearchStrategy(this);
        if (strategy == Strategy.AStar)
            return new AStarSearchStrategy(this);
        if (strategy == Strategy.HpaStar)
            return new HPAStarSearchStrategy(this);

        throw new RuntimeException("Strategy not implemented: " + strategy);
    }

    /***
     * does the clustering of the map.
     */
    public void cluster() {
        cluster.perform();
    }

    /***
     * 
     * @return the map on witch the path finder acts.
     */
    public SearchableMap getMap() {
        return this.map;
    }

    /***
     * 
     * @return the instance of the clustering
     */
    public Clustering getClustering() {
        return this.cluster;
    }
}
