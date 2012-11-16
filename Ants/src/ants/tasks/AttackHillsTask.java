package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pathfinder.PathFinder;
import pathfinder.PathFinder.Strategy;
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
    private int startFlockAttackTurn = 20;
    private int startNonFlockAttackTurn = 10;
    private boolean flock = true;

    @Override
    public void doPerform() {
        if (!flock) {
            if (Ants.getAnts().getTurn() >= startNonFlockAttackTurn)
                doPerformNonFlockAttack();
        } else {
            if (Ants.getAnts().getTurn() >= startFlockAttackTurn)
                doPerformFlockAttack();
        }
    }

    private void doPerformFlockAttack() {
        for (Tile enemyHill : Ants.getWorld().getEnemyHills()) {
            for (Tile myHill : Ants.getWorld().getMyHills()) {
                if (isMissionActive(enemyHill, myHill))
                    continue;
                List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, myHill, enemyHill, 40);
                if (path != null) {
                    // concentrate Ants near my Hill
                    Tile ralleyPoint = path.size() <= 10 ? path.get(path.size() / 2) : path.get(10);
                    addMission(new AttackHillsInFlockMission(enemyHill, myHill, ralleyPoint, 3, 20));
                }
            }
        }
    }

    private boolean isMissionActive(Tile hill, Tile myHill) {
        for (Mission mission : Ants.getOrders().getMissions()) {
            if (mission instanceof AttackHillsInFlockMission) {
                if (((AttackHillsInFlockMission) mission).getHill().equals(hill)
                        || ((AttackHillsInFlockMission) mission).getStartPoint().equals(myHill))
                    return true;
            }
        }
        return false;
    }

    private void doPerformNonFlockAttack() {
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
