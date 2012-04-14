package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import ants.entities.Ant;
import ants.entities.Route;
import ants.entities.Tile;
import ants.missions.GatherFoodMission;
import ants.missions.Mission;
import ants.search.PathFinder;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

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
        Logger.debug(LogCategory.FOOD, "unemployed ants %s", Ants.getPopulation().getMyUnemployedAnts().size());
        foodRoutes = new ArrayList<Route>();
        sortedFood = new TreeSet<Tile>(Ants.getWorld().getFoodTiles());
        sortedAnts = new TreeSet<Ant>(Ants.getPopulation().getMyUnemployedAnts());

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
                List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, route.getStart(), route.getEnd());
                if (path == null)
                    continue;
                if (path.size() <= 2) {
                    // food is reachable in one step, we don't need to create a task;
                    Ants.getOrders().doMoveLocation(route.getAnt(), path);
                } else {
                    Mission mission = new GatherFoodMission(route.getAnt(), path);
                    Ants.getOrders().addMission(mission);
                    mission.execute();
                }
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }

    }
}
