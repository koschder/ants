package ants.missions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Ilk;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import api.entities.Aim;
import api.entities.Tile;

/**
 * Implements the interface Mission an handles the base tasks of a mission.
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class BaseMission implements Mission {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    protected List<Ant> ants;
    private boolean abandon = false;
    private int foodSafetyToAbandonMission = 0;

    @Override
    public boolean isAbandoned() {
        return abandon;
    }

    /**
     * Default constructor for a mission with 0 to many ants
     * 
     * @param ants
     */
    public BaseMission(Ant... ants) {
        this.ants = new ArrayList<Ant>();
        for (Ant ant : ants) {
            addAnt(ant);
        }
    }

    /**
     * Adds the ant to the mission and marks it as employed
     * 
     * @param ant
     */
    protected void addAnt(Ant ant) {
        this.ants.add(ant);
        Ants.getPopulation().addEmployedAnt(ant);
    }

    /**
     * 
     * @param ants
     * @deprecated use BaseMission(Ant... ants) instead
     */
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
        Tile nextStep = a.getPath().get(0);

        if (putMissionOrder(a, nextStep)) {
            a.getPath().remove(0);
            return true;
        }
        return false;
    }

    /**
     * Allows us to abort the mission if something disturbs the mission
     * 
     * @param ant
     *            to check
     * @param checkDefendHill
     *            is a DefendHillMission in need of ants nearby?
     * @param checkFood
     *            is food nearby?
     * @param checkEnemyAnts
     *            are enemies nearby?
     * @param checkEnemyHill
     *            is an enemy hill nearby?
     * @return a String with everything interesting nearby, an empty String if nothing was found
     */
    protected String checkEnviroment(Ant ant, boolean checkFood, boolean checkEnemyAnts, boolean checkEnemyHill,
            boolean checkDefendHill) {
        final boolean foodNearby = checkFood && isFoodNearby(ant);
        final boolean enemyIsSuperior = checkEnemyAnts && isEnemySuperior(ant);
        final boolean enemyHillNearby = checkEnemyHill && enemyHillNearby(ant);
        final boolean defendHillNearby = checkDefendHill && defendHillNearby(ant);

        return (foodNearby ? "food," : "") + (enemyIsSuperior ? "enemy," : "") + (enemyHillNearby ? "enemyHill," : "")
                + (defendHillNearby ? "defendHill," : "");
    }

    private boolean defendHillNearby(Ant ant) {
        for (Mission mission : Ants.getOrders().getMissions()) {
            if (mission instanceof DefendHillMission) {
                DefendHillMission dhm = (DefendHillMission) mission;
                int maxDistance = 10;
                if (dhm.needsMoreAnts() && isHillNearby(ant, dhm.getHill(), maxDistance))
                    return true;
            }
        }
        return false;
    }

    private boolean enemyHillNearby(Ant ant) {
        int maxDistanceOfEnemyHill = 10;
        for (Tile enemyHill : Ants.getWorld().getEnemyHills()) {
            if (isHillNearby(ant, enemyHill, maxDistanceOfEnemyHill))
                return true;
        }
        return false;
    }

    private boolean isHillNearby(Ant ant, Tile hill, int maxManhattanDistance) {
        if (Ants.getWorld().manhattanDistance(ant.getTile(), hill) < maxManhattanDistance) {
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, hill, ant.getTile(), maxManhattanDistance);
            if (path != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnemySuperior(final Ant ant) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(Ants.getWorld());
        final Tile target;
        if (ant.getPath().size() < 6)
            target = ant.getPathEnd();
        else
            target = ant.getPath().get(5);
        final int distanceToTarget = Ants.getWorld().getSquaredDistance(ant.getTile(), target);
        List<Tile> enemiesInTheWay = bfs.floodFill(target, distanceToTarget, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile) == Ilk.ENEMY_ANT
                        && Ants.getWorld().getSquaredDistance(ant.getTile(), tile) < distanceToTarget;
            }
        });
        return enemiesInTheWay.size() >= getAnts().size();
    }

    /**
     * food is nearby, and it isn't targeted yet, the ant is not moving toward it and it is reachable then we will
     * return true for 'foodNearby'
     * 
     * @param ant
     * @return
     */
    private boolean isFoodNearby(Ant ant) {
        List<Tile> foods = Ants.getWorld().isFoodNearby(ant.getTile());
        if (foods.isEmpty())
            return false; // no food nearby

        for (Tile t : foods) {
            if (ant.hasPath()) {
                int nextSteps = Math.min(4, ant.getPath().size() - 1);
                for (int i = 0; i < nextSteps; i++) {
                    if (Ants.getWorld().manhattanDistance(t, ant.getPath().get(i)) <= 1) {
                        // ant is moving towards some food, we don't have to break this mission.
                        return false;
                    }
                }
            }
            if (shouldTakeFood(ant, t))
                return true;
        }
        return false;
    }

    private boolean shouldTakeFood(Ant ant, Tile t) {
        if (Ants.getOrders().isFoodTargeted(t))
            return false;
        if (!Ants.getWorld().isEasilyReachable(ant.getTile(), t))
            return false;
        int safety = Ants.getInfluenceMap().getSafety(t);
        if (safety < foodSafetyToAbandonMission) {
            LOGGER.info("Don't abandon Mission food %s is not safe for ant %s", t, safety, ant);
            return false;
        }
        return true;
    }

    /**
     * Gather additional ants for the mission
     * 
     * @param tile
     *            the center of interest (where should the ants gather to?)
     * @param amount
     *            how many ants we need
     * @param manhattanAttractionDistance
     *            how far away should we search?
     * @return a map of gathered ants along with their path to the destination
     */
    protected Map<Ant, List<Tile>> gatherAnts(Tile tile, int amount, int manhattanAttractionDistance) {

        Map<Ant, List<Tile>> newAnts = new HashMap<Ant, List<Tile>>();
        if (amount < 1)
            return newAnts;

        // don't search more ants than we are allowed to recruit
        int maxAnts = Math.min(amount, Ants.getPopulation().getMaxAnts(getTaskType()));
        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        final List<Ant> unemployed = bfs.findUnemployedAntsInRadius(tile, maxAnts, manhattanAttractionDistance);
        if (new AntsBreadthFirstSearch.UnemployedAntGoalTest().isGoal(tile))
            unemployed.add(Ants.getPopulation().getMyAntAt(tile, true));
        // create a path for all available ants within distance
        for (Ant a : unemployed) {
            List<Tile> p = Ants.getPathFinder().search(Strategy.AStar, tile, a.getTile());
            if (p == null)
                continue;
            /*
             * we need to search a path from the hill to the ant instead of the other way around, because paths are not
             * allowed to lead over our own hill. Therefore, we need to reverse the path afterwards.
             */
            Collections.reverse(p);
            newAnts.put(a, p);
        }
        return newAnts;
    }

    /**
     * cancels the mission. (Attention: Must not have made a move this turn yet).
     */
    protected void abandonMission() {
        LOGGER.debug("Abandoning mission of type %s for ants %s", getClass().getSimpleName(), getAnts());
        abandon = true;
    }

    /**
     * puts the next move of the ant in the orders set.
     * 
     * @param ant
     * @param aim
     * @return true if the move is stored and will be executed.
     */
    protected boolean putMissionOrder(Ant ant, Aim aim) {
        return Ants.getOrders().issueOrder(ant, aim, getVisualizeInfos());
    }

    /**
     * puts a null order in the orders set, indicating the ant will not move this turn.
     * 
     * @param ant
     * @return true if the move is stored and will be executed.
     */
    protected boolean putMissionOrder(Ant ant) {
        return Ants.getOrders().issueOrder(ant, null, getVisualizeInfos());
    }

    /**
     * 
     * @return the name of the mission.
     */
    protected String getVisualizeInfos() {
        return getClass().getSimpleName();
    }

    /**
     * Abstract class must be implemented be the mission. it checks if the mission is still vaild.
     * 
     * @return true if mission is vaild
     */
    protected abstract String isSpecificMissionValid();

    /**
     * puts the next move of the ant in the orders set.
     * 
     * @param a
     * @param to
     * @return true if the move is stored and will be executed.
     */
    protected boolean putMissionOrder(Ant a, Tile to) {
        if (a.getTile().equals(to)) {
            return putMissionOrder(a);
        }
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

    /**
     * Should this mission check if it has any ants when determining if it is valid?
     * 
     * @return true if isValid() should check for ants
     */
    protected boolean checkAnts() {
        return true;
    }

    /**
     * Can this ant be relased from the mission?
     * 
     * @param a
     * @return true if the ant can be released
     */
    protected boolean isAntReleaseable(Ant a) {
        return true;
    }

    /**
     * Release ants from the mission
     * 
     * @param amount
     *            how many ants should be released
     */
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

    /**
     * Remove the given ants from this mission
     * 
     * @param antsToRelease
     */
    public void removeAnts(List<Ant> antsToRelease) {
        LOGGER.debug("Mission %s is releasing ants %s", getClass().getSimpleName(), antsToRelease);
        for (Ant a : antsToRelease) {
            a.setPath(null);
            Ants.getPopulation().removeEmployedAnt(a);
            ants.remove(a);
        }
    }

    /**
     * Performs a move in the direction of the target tile
     * 
     * @param ant
     * @param target
     * @return true if successful
     */
    protected boolean doMoveInDirection(Ant ant, Tile target) {
        List<Aim> aims = Ants.getWorld().getDirections(ant.getTile(), target);
        for (Aim a : aims) {
            if (putMissionOrder(ant, a))
                return true;
        }
        return false;
    }

    /**
     * Issues a random move for the ant
     * 
     * @param ant
     * @return true if successful
     */
    protected boolean doAnyMove(Ant ant) {
        for (Aim aim : Aim.values()) {
            if (Ants.getOrders().issueOrder(ant, aim, getClass().getSimpleName()))
                return true;
        }
        return false;
    }
}
