package ants.tasks;

import java.util.*;

import logging.*;
import pathfinder.*;
import ants.LogCategory;
import ants.entities.*;
import ants.missions.*;
import ants.state.*;
import api.entities.*;

/**
 * Searches for food and sends our ants to gather it.
 * 
 * @author kases1,kustl1
 * 
 */
public class GatherFoodTask extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOOD);

    @Override
    public void doPerform() {
        Map<Tile, Tile> foodTargets;
        List<Route> foodRoutes;
        TreeSet<Tile> sortedFood;
        TreeSet<Ant> sortedAnts;
        foodTargets = new HashMap<Tile, Tile>();
        // find close food
        LOGGER.debug("unemployed ants %s", Ants.getPopulation().getMyUnemployedAnts().size());
        foodRoutes = new ArrayList<Route>();
        sortedFood = new TreeSet<Tile>(Ants.getWorld().getFoodTiles());
        sortedAnts = new TreeSet<Ant>(Ants.getPopulation().getMyUnemployedAnts());

        for (Tile foodLoc : sortedFood) {
            for (Ant ant : sortedAnts) {
                final Tile antLoc = ant.getTile();
                if (!Ants.getWorld().isFoodNearby(antLoc))
                    continue;
                int distance = Ants.getWorld().getSquaredDistance(antLoc, foodLoc);
                Route route = new Route(antLoc, foodLoc, distance, ant);
                foodRoutes.add(route);
            }
        }
        Collections.sort(foodRoutes);
        for (Route route : foodRoutes) {
            // food not already targeted && ant not used
            if (!foodTargets.containsKey(route.getEnd()) && !foodTargets.containsValue(route.getStart())) {
                List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.Simple, route.getStart(),
                        route.getEnd());
                if (path == null)
                    path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, route.getStart(), route.getEnd());
                if (path == null)
                    continue;
                addMission(new GatherFoodMission(route.getAnt(), path));
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }

    }
}
