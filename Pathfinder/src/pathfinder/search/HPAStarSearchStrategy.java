package pathfinder.search;

import java.util.Collections;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import pathfinder.entities.Clustering;
import pathfinder.entities.DirectedEdge;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

public class HPAStarSearchStrategy extends SearchStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.HPASTAR);

    private Clustering clustering;

    public HPAStarSearchStrategy(PathFinder f, Clustering clustering) {
        super(f);
        this.clustering = clustering;
    }

    /***
     * return the best path between the from and the to SearchTarget using the HPA* algorithm. if the clustering for the
     * HPA* is not ready yet we use A*
     */
    public List<Tile> searchPath(SearchTarget from, SearchTarget to) {

        Tile start = from.getTargetTile();
        Tile end = to.getTargetTile();

        DirectedEdge edgeStart = clustering.getStartEdge(start, end);
        DirectedEdge endEdge = clustering.getStartEdge(end, start);

        if (edgeStart == null || endEdge == null) {
            LOGGER.debug("HPAstar: Clustering not avaiable, try to find path with A*");
            return findPathWithAStar(start, end, maxCost);
        }
        endEdge.reverseEdge();
        LOGGER.debug("HPAstar: Connecting edges to Cluster edge are:");
        LOGGER.debug("     for start tile %s the edge is : %s", start, edgeStart);
        LOGGER.debug("     for end tile %s the edge is : %s", end, endEdge);

        PathFinder pf = new SimplePathFinder(clustering);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, edgeStart, endEdge, maxCost);

        if (path != null) {
            Collections.reverse(path);
            path.addAll(endEdge.getPath().subList(1, endEdge.getPath().size() - 1));

        }
        return path;

    }

    private List<Tile> findPathWithAStar(SearchTarget start, SearchTarget end, int maxCost) {
        return pathFinder.search(PathFinder.Strategy.AStar, start, end, maxCost);
    }

}
