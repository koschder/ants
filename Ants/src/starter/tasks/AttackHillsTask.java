package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import starter.Ant;
import starter.Route;
import starter.Tile;

public class AttackHillsTask extends BaseTask {

    Set<Tile> enemyHills = new HashSet<Tile>();

    @Override
    public void perform() {
        for (Iterator<Tile> iterator = enemyHills.iterator(); iterator.hasNext();) {
            if (!ants.isVisible(iterator.next()))
                iterator.remove();
        }
        // add new hills to set
        for (Tile enemyHill : ants.getEnemyHills()) {
            if (!enemyHills.contains(enemyHill)) {
                enemyHills.add(enemyHill);
            }
        }
        // attack hills
        List<Route> hillRoutes = new ArrayList<Route>();
        for (Tile hillLoc : enemyHills) {
            for (Ant ant : ants.getMyUnemployedAnts()) {
                final Tile tile = ant.getTile();
                if (!ants.getOrders().containsValue(tile)) {
                    int distance = ants.getSquaredDistance(tile, hillLoc);
                    Route route = new Route(tile, hillLoc, distance, ant);
                    hillRoutes.add(route);
                }
            }
        }
        Collections.sort(hillRoutes);
        for (Route route : hillRoutes) {
            doMoveLocation(route.getAnt(), route.getEnd());
        }
    }

}
