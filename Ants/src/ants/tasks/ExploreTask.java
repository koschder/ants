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
 * @author kases1,kustl1
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

        int totalTiles = Ants.getWorld().getCols() * Ants.getWorld().getRows();
        if ((unseenTiles.size() / totalTiles) < 0.1)
            explore(invisibleTiles);
        else
            explore(unseenTiles);
    }

    private void explore(Set<Tile> tiles) {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            final Tile antLoc = ant.getTile();
            List<Route> unseenRoutes = new ArrayList<Route>();
            int minDistance = Integer.MAX_VALUE;
            for (Tile unseenLoc : tiles) {
                int distance = Ants.getWorld().getSquaredDistance(antLoc, unseenLoc);
                if (distance > MAXDISTANCE)
                    continue;
                if (distance < minDistance)
                    minDistance = distance;
                Route route = new Route(antLoc, unseenLoc, distance, ant);
                unseenRoutes.add(route);
            }
            Collections.sort(unseenRoutes);

            for (Route route : unseenRoutes) {
                if (route.getDistance() > minDistance * 2)
                    break;
                List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.Simple, route.getStart(),
                        route.getEnd());
                if (path == null)
                    continue;

                addMission(new ExploreMission(route.getAnt(), path));
                break;
            }
        }
    }

    private void removeVisibleTiles(Set<Tile> tiles) {
        for (Iterator<Tile> locIter = tiles.iterator(); locIter.hasNext();) {
            Tile next = locIter.next();
            if (Ants.getWorld().isVisible(next)) {
                locIter.remove();
            }
        }
    }

}
