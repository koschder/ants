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

    @Override
    public boolean isAbandoned() {
        return abandon;
    }

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

        if (checkAnts() && ants.size() == 0)
            return "No ants in mission";

        int liveAntsCounter = 0;
        for (Ant ant : this.ants) {
            if (isAntAlive(ant))
                liveAntsCounter++;
        }
        if (liveAntsCounter == 0 && checkAnts())
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

    protected boolean moveToNextTileOnPath(Ant a) {
        if (a == null || !a.hasPath())
            return false;
        Tile nextStep = a.getPath().remove(0);

        if (putMissionOrder(a, nextStep)) {
            return true;
        }
        return false;
    }

    /***
     * Abort mission if something disturbs the mission
     * 
     * @param ant
     *            to check
     * @param food
     *            is food nearby
     * @param enemyAnts
     *            is nearby
     * @param enemyHill
     *            is nearby
     * @return
     */
    protected String checkEnviroment(Ant ant, boolean checkFood, boolean checkEnemyAnts, boolean checkEnemyHill) {
        final boolean foodNearby = checkFood && isFoodNearby(ant);
        final boolean enemyIsMayor = false;// checkEnemyAnts && isEnemyMajor(ant);
        boolean enemyHillNearby = checkEnemyHill && enemyHillNearby(ant);

        return (foodNearby ? "food," : "") + (enemyIsMayor ? "enemy," : "") + (enemyHillNearby ? "enemyHill," : "");
    }

    private boolean enemyHillNearby(Ant ant) {
        int maxDistanceOfEnemyHill = 10;
        for (Tile enemyHill : Ants.getWorld().getEnemyHills()) {
            if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) < maxDistanceOfEnemyHill) {
                List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, ant.getTile(), enemyHill,
                        maxDistanceOfEnemyHill);
                if (path != null) {
                    return true;
                }
            }
        }
        return false;
    }

    // private boolean isEnemyMajor(Ant ant) {
    // List<Ant> enemy = ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false);
    // return enemy.size() > getAnts().size();
    // }

    /**
     * food is nearby but not on the path
     * 
     * @param ant
     * @return
     */
    private boolean isFoodNearby(Ant ant) {
        List<Tile> foods = Ants.getWorld().isFoodNearby(ant.getTile());
        if (foods.isEmpty())
            return false; // no food nearby
        else if (!ant.hasPath())
            return true; // food nearby and ant has no path

        int nextSteps = Math.min(3, ant.getPath().size() - 1);

        for (Tile t : foods) {
            for (int i = 0; i < nextSteps; i++) {
                if (Ants.getWorld().manhattanDistance(t, ant.getPath().get(i)) <= 1) {
                    // ant is moving towards food, we don't have to break this mission.
                    return false;
                }
            }
            if (Ants.getOrders().isFoodTargeted(t) || !Ants.getWorld().isEasilyReachable(ant.getTile(), t))
                continue;
            else
                return true;
        }
        return false;
    }

    protected Map<Ant, List<Tile>> gatherAnts(Tile tile, int amount, int attractionDistance) {
        Map<Ant, List<Tile>> newAnts = new HashMap<Ant, List<Tile>>();
        for (Ant a : Ants.getPopulation().getMyUnemployedAnts()) {
            // if no additional ant is available, don't try to gather any more
            if (!Ants.getPopulation().isNumberOfAntsAvailable(getTaskType(), 1)) {
                LOGGER.info("no ant ressources avaiable for point %s", tile);
                break;
            }

            if (newAnts.size() >= amount)
                break;

            if (Ants.getWorld().manhattanDistance(a.getTile(), tile) > attractionDistance) {
                LOGGER.info("Ant %s it to far away for point %s, cant gather for mission", a, tile);
                continue; // to far away
            }
            List<Tile> p = Ants.getPathFinder().search(Strategy.AStar, a.getTile(), tile);
            if (p == null)
                continue;
            newAnts.put(a, p);
        }
        return newAnts;
    }

    /***
     * cancels the mission. (Attention: Must not have made a move this turn yet).
     */
    protected void abandonMission() {
        LOGGER.debug("Abandoning mission of type %s for ants %s", getClass().getSimpleName(), getAnts());
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

    protected boolean checkAnts() {
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
            a.setPath(null);
            Ants.getPopulation().removeEmployedAnt(a);
            ants.remove(a);
        }
    }

    protected boolean doMoveInDirection(Ant ant, Tile target) {
        List<Aim> aims = Ants.getWorld().getDirections(ant.getTile(), target);
        for (Aim a : aims) {
            if (putMissionOrder(ant, a))
                return true;
        }
        return false;
    }

    protected boolean doAnyMove(Ant ant) {
        for (Aim aim : Aim.values()) {
            if (Ants.getOrders().issueOrder(ant, aim, getClass().getSimpleName()))
                return true;
        }
        return false;
    }
}
