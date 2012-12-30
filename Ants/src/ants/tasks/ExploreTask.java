package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Route;
import ants.missions.ExploreMission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * This task sends ants into unexplored areas of the map to discover new frontiers - to boldly go where no ant has gone
 * before.
 * 
 * @author kases1, kustl1
 * 
 */
public class ExploreTask extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXPLORE);
    private Set<Tile> unseenTiles;
    private Set<Tile> invisibleTiles;
    // max distance to follow
    private int MAXDISTANCE = 600;

    @Override
    public void doPerform() {
        invisibleTiles = new HashSet<Tile>();
        for (int row = 0; row < Ants.getWorld().getRows(); row++) {
            for (int col = 0; col < Ants.getWorld().getCols(); col++) {
                invisibleTiles.add(new Tile(row, col));
            }
        }

        if (unseenTiles == null)
            unseenTiles = invisibleTiles;
        // remove any tiles that can be seen, run each turn
        removeVisibleTiles(unseenTiles);
        removeVisibleTiles(invisibleTiles);
        LOGGER.debug("Invisible tiles: %s, Unseen tiles: %s", invisibleTiles.size(), unseenTiles.size());

        explore(unseenTiles, false);
        explore(invisibleTiles, true);
    }

    private void explore(Set<Tile> tiles, boolean lastRun) {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            List<Route> unseenRoutes = new ArrayList<Route>();
            int minDistance = createRoutes(tiles, ant, unseenRoutes);
            boolean success = createExploreMission(unseenRoutes, minDistance);

            if (lastRun && !success) {
                List<Route> routes = createRoutesToVisibleTiles(ant);
                success = createExploreMission(routes, MAXDISTANCE);
            }
            if (lastRun && !success)
                LOGGER.debug("Could not create explore mission for ant %s", ant);
        }
    }

    private List<Route> createRoutesToVisibleTiles(Ant ant) {
        List<Route> routes = new ArrayList<Route>();
        for (Tile tile : Ants.getWorld().getVisibleTiles(ant)) {
            if (Ants.getWorld().getMyHills().contains(tile))
                continue;
            int distance = Ants.getWorld().getSquaredDistance(ant.getTile(), tile);
            Route route = new Route(ant.getTile(), tile, distance, ant);
            routes.add(route);
        }
        Collections.sort(routes);
        Collections.reverse(routes);
        return routes;
    }

    private boolean createExploreMission(List<Route> routes, int minDistance) {
        for (Route route : routes) {
            if (route.getDistance() > minDistance * 2)
                break;
            List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.Simple, route.getStart(), route.getEnd());
            LOGGER.trace("Explore route: %s, path = %s", route, path);
            if (path == null)
                continue;

            addMission(new ExploreMission(route.getAnt(), path));
            return true;

        }
        return false;
    }

    private int createRoutes(Set<Tile> tiles, Ant ant, List<Route> unseenRoutes) {
        int minDistance = Integer.MAX_VALUE;
        for (Tile unseenLoc : tiles) {
            int distance = Ants.getWorld().getSquaredDistance(ant.getTile(), unseenLoc);
            if (distance > MAXDISTANCE)
                continue;
            if (distance < minDistance)
                minDistance = distance;
            Route route = new Route(ant.getTile(), unseenLoc, distance, ant);
            unseenRoutes.add(route);
        }
        Collections.sort(unseenRoutes);
        return minDistance;
    }

    private void removeVisibleTiles(Set<Tile> tiles) {
        for (Iterator<Tile> locIter = tiles.iterator(); locIter.hasNext();) {
            Tile next = locIter.next();
            if (Ants.getWorld().isVisible(next)) {
                locIter.remove();
            }
        }
    }

    @Override
    public Type getType() {
        return Type.EXPLORE;
    }
}
