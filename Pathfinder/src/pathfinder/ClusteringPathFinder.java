package pathfinder;

import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.HPAStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;
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

}
