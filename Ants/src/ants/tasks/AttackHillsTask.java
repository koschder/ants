package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




import ants.entities.Ant;
import ants.entities.Route;
import ants.missions.AttackHillMission;
import ants.search.AntsPathFinder;
import ants.state.Ants;
import api.Tile;

/**
 * Task that identifies enemy hills suitable for attacking and sends Ants to attack them.
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillsTask extends BaseTask {

    private int cutoff = 100;

    @Override
    public void perform() {
        // attack hills
        List<Route> hillRoutes = new ArrayList<Route>();
        for (Tile hillLoc : Ants.getWorld().getEnemyHills()) {
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
            List<Tile> path =Ants.getPathFinder().bestPath(AntsPathFinder.A_STAR, route.getStart(), route.getEnd());
            if (path != null)
                Ants.getOrders().addMission(new AttackHillMission(route.getAnt(), path));
        }
    }

}
