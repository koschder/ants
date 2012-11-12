package ants.missions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Aim;
import api.entities.Tile;

/***
 * Implements the interface Mission an handles the base tasks of a mission.
 * 
 * @author kaeserst
 * 
 */
public abstract class BaseMission implements Mission {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    private boolean abandon = false;

    @Override
    public boolean isValid() {
        if (abandon)
            return false;

        if (isCheckAnts() && getAnts().size() == 0)
            return false;

        for (Ant ant : this.getAnts()) {
            if (!isAntAlive(ant))
                return false;
        }
        return isSpecificMissionValid();
    }

    protected List<Ant> getAnts() {
        return Collections.unmodifiableList(Ants.getOrders().getAnts(this));
    }

    protected void addAnt(Ant ant) {
        Ants.getOrders().addMission(this, ant);
    }

    private boolean isAntAlive(Ant ant) {
        boolean antIsAlive = (Ants.getWorld().getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            LOGGER.debug("isMissionValid(): no ant at %s", ant.getTile());
        }
        return antIsAlive;
    }

    protected Map<Ant, List<Tile>> gatherAnts(Tile tile, int amount, int attractionDistance) {
        Map<Ant, List<Tile>> antsNearBy = new HashMap<Ant, List<Tile>>();
        for (Ant a : Ants.getPopulation().getMyUnemployedAnts()) {
            // if (a.getMission() != null) {
            // LOGGER.info("skip Ant %s, it has already a mission.", a);
            // continue;
            // }
            if (getAnts().size() == amount)
                break;

            if (Ants.getWorld().manhattanDistance(a.getTile(), tile) > attractionDistance) {
                LOGGER.info("Ant %s it to far away for point %s, cant gather for mission", a, tile);
                continue; // to far away
            }
            List<Tile> p = Ants.getPathFinder().search(Strategy.AStar, a.getTile(), tile);
            if (p == null)
                continue;
            antsNearBy.put(a, p);
        }
        return antsNearBy;
    }

    /***
     * cancels the mission.
     */
    protected void abandonMission() {
        LOGGER.debug("Abandoning mission of type %s", getClass().getSimpleName());
        abandon = true;
    }

    /***
     * puts the next move of the ant in the orders set.
     * 
     * @param ant
     * @param aim
     * @return true if the move is stored and will be executed.
     */
    protected boolean putMissionOrder(Ant ant, Aim aim) {
        return Ants.getOrders().issueOrder(ant, aim, getVisualizeInfos());
    }

    protected boolean putMissionOrder(Ant ant) {
        return Ants.getOrders().issueOrder(ant, null, getVisualizeInfos());
    }

    /***
     * 
     * @return the name of the mission.
     */
    protected String getVisualizeInfos() {
        return getClass().getSimpleName();
    }

    /***
     * Abstract class must be implemented be the mission. it checks if the mission is still vaild.
     * 
     * @return true if mission is vaild
     */
    protected abstract boolean isSpecificMissionValid();

    protected boolean putMissionOrder(Ant a, Tile to) {
        if (a.getTile().equals(to)) {
            return putMissionOrder(a);
        } // TODO fix for flock mission, discuss with luke
        List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), to);
        if (aims.size() != 1)
            throw new RuntimeException(String.format("Ant cannot move from %s to %s", a.getTile(), to));

        return putMissionOrder(a, aims.get(0));

    }

    @Override
    public void setup() {
        for (Ant ant : this.getAnts()) {
            ant.setup();
            if (!isAntAlive(ant)) {
                Ants.getOrders().releaseAnt(ant);
            }
        }
    }

    protected boolean isCheckAnts() {
        return true;
    }

    protected boolean isAntReleaseable(Ant a) {
        return true;
    }

    protected void releaseAnts(int amount) {
        if (amount <= 0)
            return;
        for (Ant a : getAnts()) {
            if (isAntReleaseable(a))
                Ants.getOrders().releaseAnt(a);
        }

    }

}
