package ants.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.missions.BaseMission;
import ants.missions.Mission;
import ants.tasks.BaseTask;
import ants.util.LiveInfo;
import api.entities.Aim;
import api.entities.Move;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

/**
 * This class tracks all orders and missions for our ants. It ensures that no conflicting orders are given.
 * 
 * @author kases1,kustl1
 * 
 */
public class Orders {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.ORDERS);
    private static final Logger LOGGER_MISSIONS = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    private static final Logger LOGGER_TASKS = LoggerFactory.getLogger(LogCategory.EXECUTE_TASKS);

    // for each target tile, mark which ant is moving there
    private Map<Tile, Move> targets = new HashMap<Tile, Move>();
    // for each ant, mark which tile it will move to
    private Map<Ant, Tile> orders = new HashMap<Ant, Tile>();
    // for each ant, mark which mission it is on
    private Map<Ant, Mission> missions = new HashMap<Ant, Mission>();

    public List<Ant> getAnts(Mission mission) {
        List<Ant> ants = new ArrayList<Ant>();
        for (Entry<Ant, Mission> entry : missions.entrySet()) {
            if (entry.getValue().equals(mission))
                ants.add(entry.getKey());
        }
        return ants;
    }

    public void removeMission(Mission mission) {
        Set<Ant> antsToRemove = new HashSet<Ant>();
        for (Iterator<Entry<Ant, Mission>> it = missions.entrySet().iterator(); it.hasNext();) {
            Entry<Ant, Mission> entry = it.next();
            if (entry.getValue().equals(mission)) {
                antsToRemove.add(entry.getKey());
                LOGGER_MISSIONS.debug("removing mission %s for ant %s", entry.getValue(), entry.getKey());
                // it.remove();
            }
        }
        for (Ant ant : antsToRemove) {
            missions.remove(ant);
        }
    }

    public void releaseAnt(Ant ant) {
        LOGGER_MISSIONS.debug("Releasing ant %s", ant);
        missions.remove(ant);
    }

    /**
     * Clears all turn-scoped state (i.e. the orders); the missions are tracked across turns.
     */
    public void clearState() {
        targets.clear();
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : Ants.getWorld().getMyHills()) {
            targets.put(myHill, null);
        }
        for (Iterator<Entry<Ant, Mission>> it = missions.entrySet().iterator(); it.hasNext();) {
            Entry<Ant, Mission> entry = it.next();
            LOGGER_MISSIONS.debug("Ant %s is on Mission %s", entry.getKey(), entry.getValue().getClass()
                    .getSimpleName());
        }

        Map<Ant, Mission> newMissions = new HashMap<Ant, Mission>();
        for (Entry<Ant, Mission> entry : missions.entrySet()) {
            newMissions.put(entry.getKey(), entry.getValue());
        }
        missions = newMissions;
        for (Iterator<Entry<Ant, Mission>> it = missions.entrySet().iterator(); it.hasNext();) {
            Entry<Ant, Mission> entry = it.next();
            LOGGER_MISSIONS.debug("Ant %s is on Mission %s", entry.getKey(), entry.getValue().getClass()
                    .getSimpleName());
        }
    }

    /**
     * Move the ant to the next tile in the given direction. This might fail if the tile is occupied, not passable, or
     * if another ant was already sent there.
     * 
     * If the order is placed successfully, the nextTile attribute of the ant is updated and the ant is marked as
     * employed.
     * 
     * @param ant
     * @param direction
     * @param issuer
     *            who gave the order? for logging purposes only
     * @return true if the order was successfully placed
     */
    public boolean issueOrder(Ant ant, Aim direction, String issuer) {
        // Track all moves, prevent collisions
        Tile newLoc = ant.getTile();
        if (direction != null)
            newLoc = Ants.getWorld().getTile(ant.getTile(), direction);

        if (isFreeForNextMove(newLoc) || direction == null) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s Order:%s<br/> ant: %s", issuer,
                    direction, ant.getTile());
            LOGGER_TASKS.debug("%1$s: Moving ant from %2$s to %3$s", issuer, ant.getTile(), newLoc);
            targets.put(newLoc, new Move(ant.getTile(), direction));
            orders.put(ant, newLoc);
            ant.setNextTile(newLoc);
            // Ants.getPopulation().addEmployedAnt(ant);
            ant.resetTurnsWaited();
            return true;
        } else {
            ant.incrementTurnsWaited();
            LOGGER_TASKS.debug("Move is not possible %s to %s", ant, newLoc);
        }
        return false;
    }

    private boolean isFreeForNextMove(Tile nextLocation) {
        String sLog = "isFreeForNextMove: ";
        // there is already an order heading to this field
        boolean hasOrder = targets.containsKey(nextLocation);
        if (hasOrder)
            return false;

        if (Ants.getWorld().getIlk(nextLocation).isUnoccupied())
            return true;
        // TODO why is here getEmployedAnts() not filled??
        // the field is occupied at the moment, but the ant is moving away with the next move.
        // if (Ants.getPopulation().getEmployedAnts().contains(nextLocation)) {
        sLog += "check if ant will go away neighbrs are: ";
        List<SearchTarget> neighbours = Ants.getWorld().getSuccessor(nextLocation, false);
        sLog += neighbours;
        for (SearchTarget neighbour : neighbours) {
            if (targets.containsKey(neighbour.getTargetTile())) {
                sLog += "there is a order to neighbour " + neighbour;
                // there is a move where a ant goes to a neighbour cell, maybe it's the ant of "nextlocation"
                Move m = targets.get(neighbour);
                sLog += "move comes from " + m.getTile();
                if (m.getTile().equals(nextLocation)) {
                    // yes it is the ant

                    return true;
                }
            }
        }
        // } else {
        // sLog += "no employeed ant on field " + nextLocation + " till now ["
        // + Ants.getPopulation().getEmployedAnts().size() + "] ";
        // }
        LOGGER_TASKS.debug(sLog);
        return false;
    }

    /**
     * Adds a mission to the list and executes its first step.
     * 
     * @param newMission
     */
    public void addMission(Mission newMission, Ant... ants) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (!(stackTrace[2].getClassName().equals(BaseTask.class.getCanonicalName()) || stackTrace[2].getClassName()
                .equals(BaseMission.class.getCanonicalName()))) {
            throw new IllegalStateException("addMission must only be called from BaseTask or BaseMission");
        }
        for (Ant ant : ants) {
            missions.put(ant, newMission);
            LOGGER_MISSIONS.debug("Ant %s sent on mission: %s", ant, newMission);
        }
        newMission.execute();
    }

    /**
     * Prints the orders to the SystemOutputStream (sends them to the game engine).
     */
    public void issueOrders() {
        for (Move move : targets.values()) {
            if (move != null && move.getDirection() != null) {
                final String order = "o " + move.getTile().getRow() + " " + move.getTile().getCol() + " "
                        + move.getDirection().getSymbol();
                LOGGER.debug("Issuing order: %s", order);
                System.out.println(order);
            } else {
                LOGGER.debug("NOT Issuing order: %s", move.getTile());
            }
        }
    }

    /*
     * Accessors
     */

    public Map<Tile, Move> getOrders() {
        return targets;
    }

    public Set<Mission> getMissions() {
        return Collections.unmodifiableSet(new HashSet<Mission>(missions.values()));
    }

    public Set<Ant> getEmployedAnts() {
        Set<Ant> employedAnts = new HashSet<Ant>(orders.keySet());
        employedAnts.addAll(missions.keySet());
        return employedAnts;
    }
}
