package ants.missions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Route;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Tile;

/***
 * Mission for gathering a food tile
 * 
 * @author kustl1, kases1
 * 
 */
public class GatherFoodMission extends BaseMission {

    // Tile food;
    Map<Tile, Ant> antsOnFood;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOOD);

    public GatherFoodMission() {
        super();

    }

    /***
     * is valid as long the food is on the map
     */
    @Override
    public String isSpecificMissionValid() {
        return null;
    }

    @Override
    public String isValid() {
        return null;
    }

    @Override
    public String toString() {
        return "GatherFoodMission: [ant=" + ants + "]";
    }

    @Override
    public Type getTaskType() {
        return Type.GATHER_FOOD;
    }

    @Override
    public void execute() {
        antsOnFood = new HashMap<Tile, Ant>();
        LOGGER.debug("execute GatherFoodMission: ants on food are");
        for (Ant a : ants) {

            LOGGER.debug("Ant: %s food: %s", a, a.getPathEnd());
            antsOnFood.put(a.getPathEnd(), a);

        }
        // LOGGER.debug("size of entrySet is: %s", antsOnFood.size());
        // for (Entry<Tile, Ant> entry : antsOnFood.entrySet()) {
        // LOGGER.debug("-Ant: %s food: %s", entry.getValue(), entry.getKey());
        // }

        checkAntsRoutes();
        gatherAnts();
        moveAnts();

        if (antsOnFood.size() != ants.size())
            throw new IllegalArgumentException("antsOnFoods is not vaild");

    }

    private void checkAntsRoutes() {
        List<Ant> antsToRelease = new ArrayList<Ant>();
        List<Ant> antsFoodFound = new ArrayList<Ant>();
        for (Ant a : ants) {
            if (!Ants.getWorld().getIlk(a.getPathEnd()).isFood())
                antsToRelease.add(a);
            else if (a.getPath().size() < 2)
                antsFoodFound.add(a);

        }
        LOGGER.debug("releasedAnts ants (the cake is a lie) %s", antsToRelease);
        LOGGER.debug("releasedAnts ants (the cake was nice) %s", antsFoodFound);
        removeAnts(antsToRelease, true);
        removeAnts(antsFoodFound, true);
    }

    public void removeAnts(List<Ant> antsToRelease, boolean removeOfKeySet) {
        for (Ant a : antsToRelease) {
            if (removeOfKeySet)
                antsOnFood.remove(a.getPathEnd());
            a.setPath(null);
        }
        super.removeAnts(antsToRelease);
    }

    private void moveAnts() {
        List<Ant> antsToRelease = new ArrayList<Ant>();
        for (Ant a : ants) {
            LOGGER.debug("FoodMission: move ant %s path is %s", a, a.getPath());
            if (!moveToNextTile(a))
                antsToRelease.add(a);
        }
        LOGGER.debug("releasedAnts ants (cannot move them) %s", antsToRelease);
        removeAnts(antsToRelease, true);
    }

    protected boolean moveToNextTile(Ant a) {
        if (a == null || !a.hasPath())
            return false;
        Tile nextStep = a.getPath().get(0);
        if (putMissionOrder(a, nextStep)) {
            LOGGER.debug("FoodMission: move ant %s towards food %s nextstep: %s", a, a.getPathEnd(), nextStep);
            a.getPath().remove(0);
            return true;
        }
        LOGGER.debug("FoodMission:cannot move ant %s to nextstep: %s", a, nextStep);
        return false;
    }

    public void gatherAnts() {
        // Map<Tile, Tile> foodTargets;
        List<Ant> releasedAnts = new ArrayList<Ant>();
        // find close food
        LOGGER.debug("unemployed ants %s ants on food: %s", Ants.getPopulation().getMyUnemployedAnts().size(),
                antsOnFood.size());
        List<Route> foodRoutes = new ArrayList<Route>();
        TreeSet<Tile> sortedFood = new TreeSet<Tile>(Ants.getWorld().getFoodTiles());
        TreeSet<Ant> sortedAnts = new TreeSet<Ant>(Ants.getPopulation().getMyUnemployedAnts());

        for (Tile foodLoc : sortedFood) {
            // fix if food spawns on hill
            if (Ants.getWorld().getMyHills().contains(foodLoc))
                continue;
            // do not consider food, where are already existing routes smaller than 5
            if (antsOnFood.get(foodLoc) != null && antsOnFood.get(foodLoc).getPath().size() < 5)
                continue;

            for (Ant ant : sortedAnts) {
                final Tile antLoc = ant.getTile();
                int distance = Ants.getWorld().getSquaredDistance(antLoc, foodLoc);
                Route route = new Route(antLoc, foodLoc, distance, ant);
                foodRoutes.add(route);
            }
        }
        Collections.sort(foodRoutes);
        for (Route route : foodRoutes) {

            // ant already used.
            // if (!foodTargets.containsKey(route.getEnd()) && !foodTargets.containsValue(route.getStart())) {
            List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.Simple, route.getStart(), route.getEnd());
            if (path == null)
                path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, route.getStart(), route.getEnd(), 20);
            if (path == null)
                continue;

            if (antsOnFood.containsKey(route.getEnd())) {
                // we have already a mission to this food tile, check if this one is smaller.
                if (path.size() > antsOnFood.get(route.getEnd()).getPath().size())
                    continue;
                Ant a = antsOnFood.remove(route.getEnd());
                LOGGER.debug("better food route for food %s releaseAnt %s", a.getPathEnd(), a);
                releasedAnts.add(a);
            }
            if (antsOnFood.containsValue(route.getAnt())) {
                // we have already a route for this ant, check if this one is smaller.
                Ant a = ants.get(ants.indexOf(route.getAnt()));
                if (path.size() > a.getPath().size())
                    continue;
                LOGGER.debug("better food route for ant %s path is %s smaller", a, a.getPath().size() - path.size());
                antsOnFood.remove(a.getPathEnd());
                // (release ant not needed, we keep the ant in the mission (only the food target changes)
            }

            Ant a = route.getAnt();
            a.setPath(path);
            if (!ants.contains(a))
                ants.add(a);
            if (releasedAnts.contains(a))
                releasedAnts.remove(a);
            antsOnFood.put(route.getEnd(), a);

            LOGGER.debug("new food route for ants %s food %s path size: %s AntsOnFood %s", a, route.getEnd(),
                    path.size(), antsOnFood.size());
            // foodTargets.put(route.getEnd(), route.getStart());
        }
        LOGGER.debug("releasedAnts ants (better routes found) %s", releasedAnts);
        removeAnts(releasedAnts, false);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

}
