package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Logger.LogCategory;
import starter.Route;
import starter.Tile;
import starter.mission.GatherFoodMission;

public class GatherFoodTask extends BaseTask {

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
        Logger.debug(LogCategory.FOOD, "unemployed ants %s", Ants.getAnts().getMyUnemployedAnts().size());
        foodRoutes = new ArrayList<Route>();
        sortedFood = new TreeSet<Tile>(Ants.getWorld().getFoodTiles());
        sortedAnts = new TreeSet<Ant>(Ants.getAnts().getMyUnemployedAnts());

        for (Tile foodLoc : sortedFood) {
            for (Ant ant : sortedAnts) {
                final Tile antLoc = ant.getTile();
                int distance = Ants.getWorld().getSquaredDistance(antLoc, foodLoc);
                Logger.debug(LogCategory.FOOD, "Distance is %s", distance);
                // Todo distance verwirrlich, da nicht im pixel mass.
                if (distance > MAXDISTANCE)
                    continue;
                Route route = new Route(antLoc, foodLoc, distance, ant);
                foodRoutes.add(route);
            }
        }
        Collections.sort(foodRoutes);
        for (Route route : foodRoutes) {
            // food not already targeted && ant not used
            if (!foodTargets.containsKey(route.getEnd()) && !foodTargets.containsValue(route.getStart())) {
                List<Tile> list = search.bestPath(route.getStart(), route.getEnd());
                if (list == null)
                    continue;
                if (list.size() <= 2) {
                    // food is reachable in one step, we don't need to create a task;
                    doMoveLocation(route.getAnt(), list.get(0));
                } else {
                    GatherFoodMission mission = new GatherFoodMission(route.getAnt(), list);
                    Ants.getAnts().addMission(mission);
                    mission.perform();
                }
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }

    }
}
