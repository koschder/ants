package ants.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Route;
import ants.missions.GatherFoodMission;
import ants.search.AntsPathFinder;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Searches for food and sends our ants to gather it.
 * 
 * @author kases1,kustl1
 * 
 */
public class GatherFoodTask extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOOD);
    // the maximum distance an ant can be away of a food tile to catch it.
    private int MAXDISTANCE = 50;

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
                int distance = Ants.getWorld().getSquaredDistance(antLoc, foodLoc);
                LOGGER.debug("Distance is %s", distance);
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
                List<Tile> path = Ants.getPathFinder()
                        .bestPath(AntsPathFinder.SIMPLE, route.getStart(), route.getEnd());
                if (path == null)
                    path = Ants.getPathFinder().bestPath(AntsPathFinder.A_STAR, route.getStart(), route.getEnd());
                if (path == null)
                    continue;
                addMission(new GatherFoodMission(route.getAnt(), path));
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }

    }
}
