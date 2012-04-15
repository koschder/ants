package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ants.entities.Ant;
import ants.entities.Route;
import ants.entities.Tile;
import ants.missions.AttackHillMission;
import ants.search.PathFinder;
import ants.state.Ants;

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
            List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, route.getStart(), route.getEnd());
            if (path != null)
                Ants.getOrders().addMission(new AttackHillMission(route.getAnt(), path));
        }
    }

}
