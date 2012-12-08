package ants.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    private Set<Mission> missions = new HashSet<Mission>();
    private Map<Tile, Ant> antsOnFood = new HashMap<Tile, Ant>();
    private Map<Tile, Move> orders = new HashMap<Tile, Move>();

    /**
     * Clears all turn-scoped state (i.e. the orders); the missions are tracked across turns.
     */
    public void clearState() {
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : Ants.getWorld().getMyHills()) {
            orders.put(myHill, new Move(myHill, null));
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
        if (isSurroundedByWater(newLoc))
            return false; // it's a trap!
        if (Ants.getWorld().getIlk(newLoc).isFood()) {
            // cant' move on a food tile, so issue dummy order here
            orders.put(ant.getTile(), new Move(ant.getTile(), null));
            ant.setNextTile(ant.getTile());
            Ants.getPopulation().addEmployedAnt(ant);
            ant.resetTurnsWaited();
            return true;
        }
        if (isFreeForNextMove(newLoc) || direction == null) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s Order:%s<br/> ant: %s", issuer,
                    direction, ant.visualizeInfo());
            LOGGER_TASKS.debug("%1$s: Moving ant from %2$s to %3$s", issuer, ant.getTile(), newLoc);
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            Ants.getPopulation().addEmployedAnt(ant);
            ant.resetTurnsWaited();
            return true;
        } else {
            ant.incrementTurnsWaited();
            LOGGER_TASKS.debug("Move is not possible %s to %s", ant, newLoc);
        }
        return false;
    }

    private boolean isSurroundedByWater(Tile tile) {
        int unpassableNeighbourTiles = 0;
        for (Aim aim : Aim.values()) {
            if (!Ants.getWorld().isPassable(Ants.getWorld().getTile(tile, aim)))
                unpassableNeighbourTiles++;
        }
        return unpassableNeighbourTiles >= 3;
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
        List<SearchTarget> neighbours = Ants.getWorld().getSuccessorsForPathfinding(nextLocation, false);
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
        if (newMission.isValid() != null) {
            LOGGER_MISSIONS.debug("Mission %s (Ants: %s) is not valid because %s, not adding it.", newMission
                    .getClass().getSimpleName(), newMission.getAnts(), newMission.isValid());
            removeAnts(newMission);
            return;
        }
        executeMissions(Arrays.asList(newMission));
        if (!newMission.isAbandoned() && missions.add(newMission)) {
            LOGGER_MISSIONS.debug("New mission created: %s, Ants: %s", newMission.getClass().getSimpleName(),
                    newMission.getAnts());
        }
    }

    public void executeMissions(List<Mission> missions) {
        for (Iterator<Mission> it = missions.iterator(); it.hasNext();) {
            Mission mission = it.next();
            // LOGGER.debug("mission: %s", mission);
            if (mission.isComplete()) {
                removeAnts(mission);
                getMissions().remove(mission);
                // it.remove();
                LOGGER_MISSIONS.debug("Mission removed, its complete: %s (Ants: %s)", mission.getClass()
                        .getSimpleName(), mission.getAnts());
                continue;
            }
            String valid = mission.isValid();
            if (valid == null) {
                mission.execute();
                if (mission.isAbandoned()) {
                    valid = "mission abadoned.";
                } else {
                    LOGGER_MISSIONS.debug("Mission performed: %s, Ants: %s", mission.getClass().getSimpleName(),
                            mission.getAnts());
                }
            }
            if (valid != null) {
                LOGGER_MISSIONS.debug("Mission %s (Ants: %s) not valid because: %s. Mission is removed.", mission
                        .getClass().getSimpleName(), mission.getAnts(), valid);
                removeAnts(mission);
                getMissions().remove(mission);
                // it.remove();
            }
        }
    }

    private void removeAnts(Mission mission) {
        for (Ant ant : mission.getAnts()) {
            Ants.getPopulation().removeEmployedAnt(ant);
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

    public Map<Tile, Ant> getAntsOnFood() {
        return antsOnFood;
    }

    public boolean isFoodTargeted(Tile food) {
        return antsOnFood.containsKey(food);
    }
}
