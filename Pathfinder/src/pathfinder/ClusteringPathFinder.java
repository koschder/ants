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
import api.pathfinder.SearchableMap;

/***
 * this is the main class of the pathfinder framework
 * 
 * @author kaeserst
 * 
 */
public class ClusteringPathFinder extends SimplePathFinder {

    private Clustering cluster;

    // Default constructor
    public ClusteringPathFinder(SearchableMap map, int clusterSize, ClusterType clusterType) {
        super(map);
        initClustering(clusterSize, clusterType);
    }

    // called every turn, updates clustering
    @Override
    public void update() {
        cluster.updateClusters();
    }

    /***
     * 
     * @return the instance of the clustering
     */
    public Clustering getClustering() {
        return this.cluster;
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

    @Override
    protected SearchStrategy getStrategy(Strategy strategy) {
        if (strategy == Strategy.Simple)
            return new SimpleSearchStrategy(this);
        if (strategy == Strategy.AStar)
            return new AStarSearchStrategy(this);
        if (strategy == Strategy.HpaStar)
            return new HPAStarSearchStrategy(this, cluster);

        throw new RuntimeException("Strategy not implemented: " + strategy);
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
    private void initClustering(int clusterSize, ClusterType clusterType) {

        cluster = new Clustering(this, clusterSize, map.getRows(), map.getCols());
        cluster.setClusterType(clusterType);
        cluster.setWorldType(map.getWorldType());

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

}
