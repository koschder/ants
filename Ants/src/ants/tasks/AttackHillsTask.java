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
import ants.missions.AttackHillMission;
import ants.search.PathFinder;
import ants.state.Ants;

public class AttackHillsTask extends BaseTask {

    Set<Tile> enemyHills = new HashSet<Tile>();
    private int cutoff = 100;

    @Override
    public void perform() {
        // remove hills we can't see or that are captured
        for (Iterator<Tile> iterator = enemyHills.iterator(); iterator.hasNext();) {
            final Tile hill = iterator.next();
            if (!Ants.getWorld().isVisible(hill) || !Ants.getWorld().getEnemyHills().contains(hill))
                iterator.remove();
        }
        // add new hills to set
        for (Tile enemyHill : Ants.getWorld().getEnemyHills()) {
            if (!enemyHills.contains(enemyHill)) {
                enemyHills.add(enemyHill);
            }
        }
        // attack hills
        List<Route> hillRoutes = new ArrayList<Route>();
        for (Tile hillLoc : enemyHills) {
            for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
                final Tile tile = ant.getTile();
                if (!Ants.getOrders().getOrders().containsValue(tile)) {
                    int distance = Ants.getWorld().getSquaredDistance(tile, hillLoc);
                    if (distance > cutoff)
                        continue;
                    Route route = new Route(tile, hillLoc, distance, ant);
                    hillRoutes.add(route);
                }
            }
        }
        Collections.sort(hillRoutes);
        for (Route route : hillRoutes) {
            List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, route.getStart(), route.getEnd());
            if (path != null)
                Ants.getOrders().addMission(new AttackHillMission(route.getAnt(), path));
        }
    }

}
