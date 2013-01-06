package ants.missions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Route;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Tile;

/**
 * Mission for gathering a food tile
 * 
 * @author kases1, kustl1
 * 
 */
public class GatherFoodMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOOD);

    /**
     * Default constructor
     */
    public GatherFoodMission() {
        super();
    }

    /**
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
        Ants.getOrders().getAntsOnFood().clear();
        LOGGER.debug("execute GatherFoodMission: ants on food are");
        for (Ant a : ants) {

            LOGGER.debug("Ant: %s food: %s", a, a.getPathEnd());
            Ants.getOrders().getAntsOnFood().put(a.getPathEnd(), a);

        }

        // check the existing routes, if they are still valid.
        checkAntsRoutes();
        // gather new ants
        gatherAnts();
        // move the ants
        moveAnts();

        if (Ants.getOrders().getAntsOnFood().size() != ants.size())
            throw new IllegalArgumentException("Ants.getOrders().getAntsOnFood()s is not vaild");

    }

    private void checkAntsRoutes() {
        List<Ant> antsToRelease = new ArrayList<Ant>();
        List<Ant> antsFoodFound = new ArrayList<Ant>();
        for (Ant a : ants) {
            if (!a.hasPath()) {
                LOGGER.error("ant %s on food route has no path", a);
                antsToRelease.add(a);
            } else if (!Ants.getWorld().getIlk(a.getPathEnd()).isFood())
                antsToRelease.add(a);
            else if (a.getPath().size() < 2)
                antsFoodFound.add(a);

        }
        LOGGER.debug("releasedAnts ants (the cake is a lie) %s", antsToRelease);
        LOGGER.debug("releasedAnts ants (the cake was nice) %s", antsFoodFound);
        removeAnts(antsToRelease, true);
        removeAnts(antsFoodFound, true);
    }

    private void removeAnts(List<Ant> antsToRelease, boolean removeOfKeySet) {
        for (Ant a : antsToRelease) {
            if (removeOfKeySet)
                Ants.getOrders().getAntsOnFood().remove(a.getPathEnd());
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

    private boolean moveToNextTile(Ant a) {
        if (moveToNextTileOnPath(a)) {
            return true;
        } else if (a.hasPath()) {
            // try to recalculate the path, to turn small obstacles
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, a.getTile(), a.getPathEnd(),
                    a.getPath().size() + 2);
            if (path == null || path.size() == 0) {
                return false;
            }
            a.setPath(path);
            if (moveToNextTileOnPath(a))
                return true;
        }

        LOGGER.debug("FoodMission:cannot move ant %s on path: %s", a, a.getPath());
        return false;
    }

    private void gatherAnts() {
        // Map<Tile, Tile> foodTargets;
        List<Ant> releasedAnts = new ArrayList<Ant>();
        // find close food
        LOGGER.debug("unemployed ants %s ants on food: %s", Ants.getPopulation().getMyUnemployedAnts().size(), Ants
                .getOrders().getAntsOnFood().size());
        List<Route> foodRoutes = new ArrayList<Route>();
        TreeSet<Tile> sortedFood = new TreeSet<Tile>(Ants.getWorld().getFoodTiles());
        for (Tile foodTile : sortedFood) {
            // fix if food spawns on hill
            if (Ants.getWorld().getMyHills().contains(foodTile))
                continue;
            // do not consider food, where are already existing routes smaller than 5
            if (Ants.getOrders().getAntsOnFood().get(foodTile) != null
                    && Ants.getOrders().getAntsOnFood().get(foodTile).getPath().size() < 5)
                continue;

            Ant nearestUnemployed = new AntsBreadthFirstSearch(Ants.getWorld()).findMyClosestUnemployedAnt(foodTile,
                    500);
            if (nearestUnemployed == null) {
                LOGGER.debug("Could not find ant for food at %s", foodTile);
                continue;
            }
            LOGGER.debug("Found ant %s to gather food at  %s", nearestUnemployed, foodTile);
            final Tile antLoc = nearestUnemployed.getTile();
            int distance = Ants.getWorld().getSquaredDistance(antLoc, foodTile);
            Route route = new Route(antLoc, foodTile, distance, nearestUnemployed);
            foodRoutes.add(route);
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

            if (Ants.getOrders().getAntsOnFood().containsKey(route.getEnd())) {
                // we have already a mission to this food tile, check if this one is smaller.
                if (path.size() > Ants.getOrders().getAntsOnFood().get(route.getEnd()).getPath().size())
                    continue;
                Ant a = Ants.getOrders().getAntsOnFood().remove(route.getEnd());
                LOGGER.debug("better food route for food %s releaseAnt %s", a.getPathEnd(), a);
                releasedAnts.add(a);
            }
            if (Ants.getOrders().getAntsOnFood().containsValue(route.getAnt())) {
                // we have already a route for this ant, check if this one is smaller.
                Ant a = ants.get(ants.indexOf(route.getAnt()));
                if (path.size() > a.getPath().size())
                    continue;
                LOGGER.debug("better food route for ant %s path is %s smaller", a, a.getPath().size() - path.size());
                Ants.getOrders().getAntsOnFood().remove(a.getPathEnd());
                // (release ant not needed, we keep the ant in the mission (only the food target changes)
            }

            Ant a = route.getAnt();
            a.setPath(path);
            if (!ants.contains(a))
                ants.add(a);
            if (releasedAnts.contains(a))
                releasedAnts.remove(a);
            Ants.getOrders().getAntsOnFood().put(route.getEnd(), a);

            LOGGER.debug("new food route for ants %s food %s path size: %s Ants.getOrders().getAntsOnFood() %s", a,
                    route.getEnd(), path.size(), Ants.getOrders().getAntsOnFood().size());
            // foodTargets.put(route.getEnd(), route.getStart());
        }
        LOGGER.debug("releasedAnts ants (better routes found) %s", releasedAnts);
        removeAnts(releasedAnts, false);
    }

    @Override
    public boolean isComplete() {
        // gathering food is never complete
        return false;
    }

}
