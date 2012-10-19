package ants.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Move;
import ants.missions.Mission;
import ants.tasks.BaseTask;
import ants.util.LiveInfo;
import api.Aim;
import api.Tile;

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
        Tile newLoc = Ants.getWorld().getTile(ant.getTile(), direction);
        if (Ants.getWorld().getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s ant: %s", issuer, ant.getTile());
            LOGGER_TASKS.debug("%1$s: Moving ant from %2$s to %3$s", issuer, ant.getTile(), newLoc);
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            Ants.getPopulation().addEmployedAnt(ant);
            return true;
        }
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
            if (move != null) {
                final String order = "o " + move.getTile().getRow() + " " + move.getTile().getCol() + " "
                        + move.getDirection().getSymbol();
                LOGGER.debug("Issuing order: %s", order);
                System.out.println(order);
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
