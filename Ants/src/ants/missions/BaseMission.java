package ants.missions;

import java.util.ArrayList;
import java.util.Collection;
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
    protected List<Ant> ants;
    private boolean abandon = false;

    public BaseMission(Ant... ants) {
        this.ants = new ArrayList<Ant>();
        for (Ant ant : ants) {
            addAnt(ant);
        }
    }

    protected void addAnt(Ant ant) {
        this.ants.add(ant);
        Ants.getPopulation().addEmployedAnt(ant);
    }

    public BaseMission(Collection<Ant> ants) {
        this.ants = new ArrayList<Ant>();
        for (Ant ant : ants) {
            addAnt(ant);
        }
    }

    @Override
    public String isValid() {
        if (abandon)
            return "Abandon mission";

        if (isCheckAnts() && ants.size() == 0)
            return "No ants in mission";

        int liveAntsCounter = 0;
        for (Ant ant : this.ants) {
            if (isAntAlive(ant))
                liveAntsCounter++;
        }
        if (liveAntsCounter == 0 && isCheckAnts())
            return "No ant left alive";
        return isSpecificMissionValid();
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
            // if no additional ant is available, don't try to gather any more
            if (!Ants.getPopulation().isNumberOfAntsAvailable(getTaskType(), 1))
                break;

            if (ants.size() >= amount)
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
    protected abstract String isSpecificMissionValid();

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
        List<Ant> deadlyList = new ArrayList<Ant>();
        for (Ant ant : this.ants) {
            ant.setup();
            Ants.getPopulation().addEmployedAnt(ant);
            if (!isAntAlive(ant)) {
                deadlyList.add(ant);
            }
        }
        ants.removeAll(deadlyList);
    }

    @Override
    public List<Ant> getAnts() {
        return ants;
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
        List<Ant> antsToRelease = new ArrayList<Ant>();
        for (Ant a : ants) {
            if (isAntReleaseable(a))
                antsToRelease.add(a);
            if (antsToRelease.size() == amount)
                break;
        }
        removeAnts(antsToRelease);
    }

    public void removeAnts(List<Ant> antsToRelease) {
        for (Ant a : antsToRelease) {
            Ants.getPopulation().removeEmployedAnt(a);
            ants.remove(a);
        }
    }

    protected boolean doAnyMove(Ant ant) {
        for (Aim aim : Aim.values()) {
            if (Ants.getOrders().issueOrder(ant, aim, getClass().getSimpleName()))
                return true;
        }
        return false;
    }
}
