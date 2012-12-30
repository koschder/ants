package pathfinder;

import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.HPAStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;
import api.pathfinder.SearchableMap;

/***
 * Clustering pathfinder is designed for big maps, where simple a star reaches its frontiers. the map will be clustered
 * in smaller pieces in which the path is precalculated. if needing a path, this small path section will be combined to
 * the final path precondition: every map area to be precalculated must be visible.
 * 
 * @author kases1, kustl1
 * 
 */
public class ClusteringPathFinder extends SimplePathFinder {

    private Clustering cluster;

    /**
     * default constructor with the parameters
     * 
     * @param map
     *            searchable map
     * @param clusterSize
     *            the size of each cluster (quadratic)
     * @param clusterType
     *            (Centered or Corner)
     */
    public ClusteringPathFinder(SearchableMap map, int clusterSize, ClusterType clusterType) {
        super(map);
        initClustering(clusterSize, clusterType);
    }

    /**
     * called every turn, calculating clusters when more and more will be visible to cluster.
     * 
     */
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

    /**
     * get the strategy implementation
     * 
     * @param strategy
     * @return the new instance of the search strategy
     */
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

    /**
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
        // cluster.setWorldType(map.getWorldType());

    }

}
