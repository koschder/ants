package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import starter.Ant;
import starter.Route;
import starter.Tile;

public class GatherFoodTask extends BaseTask implements Task {

    // the maximum distance an ant can be away of a food tile to catch it.
    private int MAXDISTANCE = 50;

    @Override
    public void perform() {
        Map<Tile, Tile> foodTargets;
        List<Route> foodRoutes;
        TreeSet<Tile> sortedFood;
        TreeSet<Ant> sortedAnts;
        foodTargets = new HashMap<Tile, Tile>();
        // find close food
        foodRoutes = new ArrayList<Route>();
        sortedFood = new TreeSet<Tile>(ants.getFoodTiles());
        sortedAnts = new TreeSet<Ant>(ants.getMyUnemployedAnts());

        for (Tile foodLoc : sortedFood) {
            for (Ant ant : sortedAnts) {
                final Tile antLoc = ant.getTile();
                int distance = ants.getSquaredDistance(antLoc, foodLoc);
                // Logger.log("Distance is %s",distance);
                // Todo distance verwirrlich, da nicht im pixel mass.
                if (distance > MAXDISTANCE)
                    continue;
                Route route = new Route(antLoc, foodLoc, distance, ant);
                foodRoutes.add(route);
            }
        }
        Collections.sort(foodRoutes);
        for (Route route : foodRoutes) {
            if (!foodTargets.containsKey(route.getEnd()) && !foodTargets.containsValue(route.getStart())
                    && doMoveLocation(route.getAnt(), route.getEnd())) {
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }

    }
}
