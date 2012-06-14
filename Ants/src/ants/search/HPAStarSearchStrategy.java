package ants.search;

import java.util.List;

import ants.entities.DirectedEdge;
import ants.entities.SearchTarget;
import ants.entities.Tile;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class HPAStarSearchStrategy implements SearchStrategy {

    int maxCost = 0;

    /***
     * return the best path between the from and the to SearchTarget using the HPA* algorithm.
     * if the clustering for the HPA* is not ready yet we use A*
     */
    @Override
    public List<Tile> bestPath(SearchTarget from, SearchTarget to) {

        Tile start = from.getTargetTile();
        Tile end = to.getTargetTile();
        DirectedEdge edgeStart = Ants.getClusters().getStartEdge(start, end);
        DirectedEdge endEdge = Ants.getClusters().getStartEdge(end, start);

        if (edgeStart == null || endEdge == null) {
            Logger.debug(LogCategory.HAPstar, "HPAstar: Clustering not avaiable, try to find path with A*");
            return PathFinder.bestPath(PathFinder.A_STAR, start, end, maxCost);
        }
        endEdge.reverseEdge();
        Logger.debug(LogCategory.HAPstar, "HPAstar: Connecting edges to Cluster edge are:");
        Logger.debug(LogCategory.HAPstar, "     for start tile %s the edge is : %s", start, edgeStart);
        Logger.debug(LogCategory.HAPstar, "     for end tile %s the edge is : %s", end, endEdge);
        List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, edgeStart, endEdge, maxCost);
        if (path != null)
            path.addAll(endEdge.getPath());

        return path;

    }

    @Override
    public void setMaxCost(int i) {
        this.maxCost = i;
    }

    @Override
    public void setSearchSpace(Tile p1, Tile p2) {

    }

}
