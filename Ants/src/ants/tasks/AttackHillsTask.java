package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pathfinder.PathFinder;
import ants.entities.Ant;
import ants.entities.Route;
import ants.missions.AttackHillMission;
import ants.missions.AttackHillsInFlockMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Task that identifies enemy hills suitable for attacking and sends Ants to attack them.
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillsTask extends BaseTask {

    private int cutoff = 100;
    private boolean flock = true;

    @Override
    public void doPerform() {
        if (!flock) {
            doPerformNonFlocking();
            return;
        }
        for (Tile hillLoc : Ants.getWorld().getEnemyHills()) {
            if (isMissionActive(hillLoc))
                continue;
            List<Tile> hillZone = Ants.getWorld().getVisibleTiles(hillLoc);
            int maxSafety = Integer.MIN_VALUE;
            Tile rallyPoint = null;
            for (Tile tile : hillZone) {
                if (!Ants.getWorld().isPassable(tile))
                    continue;
                int safety = Ants.getInfluenceMap().getSafety(tile);
                if (safety > maxSafety) {
                    maxSafety = safety;
                    rallyPoint = tile;
                }
            }
            addMission(new AttackHillsInFlockMission(hillLoc, rallyPoint, 3, 10));
        }
    }

    private boolean isMissionActive(Tile hill) {
        for (Mission mission : Ants.getOrders().getMissions()) {
            if (mission instanceof AttackHillsInFlockMission) {
                if (((AttackHillsInFlockMission) mission).getHill().equals(hill))
                    return true;
            }
        }
        return false;
    }

    private void doPerformNonFlocking() {
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
        Set<Ant> employed = new HashSet<Ant>();
        for (Route route : hillRoutes) {
            List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, route.getStart(), route.getEnd());
            if (path != null && !employed.contains(route.getAnt())) {
                addMission(new AttackHillMission(route.getAnt(), path));
                employed.add(route.getAnt());
            }
        }
    }

}
