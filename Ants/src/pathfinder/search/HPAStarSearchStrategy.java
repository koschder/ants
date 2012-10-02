package pathfinder.search;

import java.util.List;

import pathfinder.PathFinder;
import pathfinder.entities.DirectedEdge;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.Tile;

import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class HPAStarSearchStrategy extends SearchStrategy {

    public HPAStarSearchStrategy(PathFinder f) {
        super(f);
    }

    /***
     * return the best path between the from and the to SearchTarget using the HPA* algorithm.
     * if the clustering for the HPA* is not ready yet we use A*
     */
    public List<Tile> search(SearchTarget from, SearchTarget to) {

        Tile start = from.getTargetTile();
        Tile end = to.getTargetTile();
        DirectedEdge edgeStart = pathFinder.getClustering().getStartEdge(start, end);
        DirectedEdge endEdge = pathFinder.getClustering().getStartEdge(end, start);

        if (edgeStart == null || endEdge == null) {
            Logger.debug(LogCategory.HAPstar, "HPAstar: Clustering not avaiable, try to find path with A*");
            return findPathWithAStar(start, end, maxCost);
        }
        endEdge.reverseEdge();
        Logger.debug(LogCategory.HAPstar, "HPAstar: Connecting edges to Cluster edge are:");
        Logger.debug(LogCategory.HAPstar, "     for start tile %s the edge is : %s", start, edgeStart);
        Logger.debug(LogCategory.HAPstar, "     for end tile %s the edge is : %s", end, endEdge);
        List<Tile> path = findPathWithAStar(edgeStart, endEdge, maxCost);
        if (path != null)
            path.addAll(endEdge.getPath());

        return path;

    }

    private List<Tile> findPathWithAStar(SearchTarget start, SearchTarget end, int maxCost) {
       return pathFinder.search(PathFinder.Strategy.AStar,start, end,maxCost);
    }

}
