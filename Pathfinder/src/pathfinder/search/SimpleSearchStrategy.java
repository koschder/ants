package pathfinder.search;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.entities.Aim;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.Tile;

public class SimpleSearchStrategy extends SearchStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.PATHFINDING);

    public SimpleSearchStrategy(PathFinder f) {
        super(f);
    }

    /***
     * the simplest algorithm for finding a path by connecting two straight lines. This alogrithm cannot be use by
     * DirectedEdges
     */
    @Override
    public List<Tile> searchPath(SearchTarget areaFrom, SearchTarget areaTo) {

        if (!(areaFrom instanceof Tile && areaTo instanceof Tile))
            throw new RuntimeException("SimpleSearchStrategy not implmented for class" + areaTo.getClass());

        Tile from = (Tile) areaFrom;
        Tile to = (Tile) areaTo;

        if (from.getCol() == to.getCol() || from.getRow() == to.getRow())
            return getStraightPath(from, to);
        List<Tile> path = getSimpleViaPath(from, to, new Tile(from.getRow(), to.getCol()));
        if (path != null)
            return path;
        return getSimpleViaPath(from, to, new Tile(to.getRow(), from.getCol()));

    }

    /***
     * try to get a path from to to with via
     * 
     * @param from
     * @param to
     * @param via
     * @return
     */
    private List<Tile> getSimpleViaPath(Tile from, Tile to, Tile via) {
        List<Tile> path = new ArrayList<Tile>();
        LOGGER.trace("calling simpleViaPath with from %s to %s via %s", from, to, via);

        List<Tile> firstLeg = getStraightPath(from, via);
        if (firstLeg == null)
            return null;
        List<Tile> secondLeg = getStraightPath(via, to);
        if (secondLeg == null)
            return null;

        path.addAll(firstLeg);
        path.addAll(secondLeg);
        if (path.size() > maxCost && maxCost != -1)
            return null; // pfad zu lang.
        return path;
    }

    /***
     * 
     * @param from
     * @param to
     * @return a straight path between two tiles or null if no path is found.
     */
    private List<Tile> getStraightPath(Tile from, Tile to) {
        List<Tile> path = new ArrayList<Tile>();

        List<Aim> directions = pathFinder.getMap().getDirections(from, to);
        if (!(directions.size() == 1))
            LOGGER.error("more than 1 direction from %s to %s", from, to);
        Aim aim = directions.get(0);

        if (!pathFinder.getMap().isPassable(pathFinder.getMap().getTile(from, aim)))
            return null;

        Tile t = from;
        while (!t.equals(to)) {
            t = pathFinder.getMap().getTile(t, aim);
            if (!pathFinder.getMap().isPassable(t) || !inSearchSpace(t))
                return null; // straight path is blocked
            path.add(t);
        }
        path.add(to);
        return path;
    }
}