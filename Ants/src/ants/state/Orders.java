package ants.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.missions.Mission;
import ants.tasks.BaseTask;
import ants.util.LiveInfo;
import api.entities.*;
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
    private Set<Mission> missions = new HashSet<Mission>();

    private Map<Tile, Move> orders = new HashMap<Tile, Move>();

    /**
     * Clears all turn-scoped state (i.e. the orders); the missions are tracked across turns.
     */
    public void clearState() {
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : Ants.getWorld().getMyHills()) {
            orders.put(myHill, null);
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

        if (isFreeForNextMove(newLoc)) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s Order:%s<br/> ant: %s", issuer,
                    direction, ant.getTile());
            LOGGER_TASKS.debug("%1$s: Moving ant from %2$s to %3$s", issuer, ant.getTile(), newLoc);
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            Ants.getPopulation().addEmployedAnt(ant);
            return true;
        } else {
            LOGGER_TASKS.debug("Move is not possible %s to %s", ant, newLoc);
        }
        return false;
    }

    private boolean isFreeForNextMove(Tile nextLocation) {
        String sLog = "isFreeForNextMove: ";
        // there is already an order heading to this field
        boolean hasOrder = orders.containsKey(nextLocation);
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
            if (orders.containsKey(neighbour.getTargetTile())) {
                sLog += "there is a order to neighbour " + neighbour;
                // there is a move where a ant goes to a neighbour cell, maybe it's the ant of "nextlocation"
                Move m = orders.get(neighbour);
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
    public void addMission(Mission newMission) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (!stackTrace[2].getClassName().equals(BaseTask.class.getCanonicalName())) {
            throw new IllegalStateException("addMission must only be called from BaseTask");
        }
        if (missions.add(newMission)) {
            newMission.execute();
            LOGGER_MISSIONS.debug("New mission created: %s", newMission);
        }
    }

    /**
     * Prints the orders to the SystemOutputStream (sends them to the game engine).
     */
    public void issueOrders() {
        for (Move move : orders.values()) {
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
        return orders;
    }

    public Set<Mission> getMissions() {
        return missions;
    }
}
