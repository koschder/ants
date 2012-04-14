package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ants.entities.Ant;
import ants.entities.Route;
import ants.entities.Tile;
import ants.search.SimpleSearchStrategy;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;


public class ExploreTask extends BaseTask {

    private Set<Tile> unseenTiles;
    private Set<Tile> invisibleTiles;
    // max distance to follow
    private int MAXDISTANCE = 600;

    @Override
    public void setup() {

        this.search = new SimpleSearchStrategy();
    }

    @Override
    public void perform() {
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
        Logger.info(LogCategory.EXPLORE, "Invisible tiles: %s, Unseen tiles: %s", invisibleTiles.size(),
                unseenTiles.size());
        long start = System.currentTimeMillis();
        int totalTiles = Ants.getWorld().getCols() * Ants.getWorld().getRows();
        if ((unseenTiles.size() / totalTiles) < 0.1)
            explore(invisibleTiles);
        else
            explore(unseenTiles);
        Logger.info(LogCategory.EXPLORE, "explore main loop took %s ms", System.currentTimeMillis() - start);
    }

    private void explore(Set<Tile> tiles) {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            final Tile antLoc = ant.getTile();
            if (!Ants.getOrders().getOrders().containsValue(antLoc)) {
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
                    if (doMoveLocation(route.getAnt(), route.getEnd())) {
                        break;
                    }
                    if (route.getDistance() > minDistance * 2)
                        break;
                }
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