package pathfinder;

import java.util.ArrayList;
import java.util.List;

import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.HPAStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;
import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableMap;

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

    public PathFinder(SearchableMap map) {
        this.map = map;
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

    public List<Tile> smoothPath(List<Tile> path) {
        return smoothPath(path, 5, 1);
    }

    public List<Tile> smoothPath(List<Tile> path, int size, int loops) {

        for (int i = 0; i < loops; i++) {
            path = smoothPath(path, size);
        }

        return path;
    }

    private List<Tile> smoothPath(List<Tile> path, int size) {
        if (path == null || path.size() < size)
            return path;
        boolean isRecursiv = false; // TODO
        int start = 0;
        int current = size;
        List<Tile> newPath = new ArrayList<Tile>();
        // do while the last tile of path is new path
        while (path.get(path.size() - 1).equals(newPath.get(path.size() - 1))) {

            List<Tile> subPath = path.subList(start, current);
            int manDist = map.manhattanDistance(subPath.get(0), subPath.get(subPath.size() - 1));

            List<Tile> newSubPath = null;
            if (manDist <= subPath.size()) {
                newSubPath = search(Strategy.AStar, subPath.get(0), subPath.get(subPath.size() - 1), subPath.size() - 1);
            }
            if (newSubPath != null) {
                if (isRecursiv) {
                    newPath.addAll(newSubPath);
                    newPath = smoothPath(newPath, newPath.size());
                }
            } else {
                newPath.addAll(subPath);
            }
            start = current;
            current = Math.min(current + size, path.size());
        }

        return newPath;
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
