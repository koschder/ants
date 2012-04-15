package ants.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Move;
import ants.entities.Tile;
import ants.missions.Mission;
import ants.tasks.Task;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Orders {

    private Set<Mission> missions = new HashSet<Mission>();

    private Map<Tile, Move> orders = new HashMap<Tile, Move>();

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
     * @return true if the order was successfully placed
     */
    public boolean putOrder(Ant ant, Aim direction) {
        // Track all moves, prevent collisions
        Tile newLoc = Ants.getWorld().getTile(ant.getTile(), direction);
        if (Ants.getWorld().getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            Logger.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s ant: %s", getTaskName(), ant);
            Logger.debug(LogCategory.EXECUTE_TASKS, "%1$s: Moving ant from %2$s to %3$s", getTaskName(), ant.getTile(),
                    newLoc);
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            Ants.getPopulation().addEmployedAnt(ant);
            return true;
        } else {
            return false;
        }
    }

    public boolean doMoveLocation(Ant ant, List<Tile> path) {
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException("Path must not be null or empty!");
        List<Aim> directions = Ants.getWorld().getDirections(ant.getTile(), path.get(0));
        for (Aim direction : directions) {
            if (Ants.getOrders().putOrder(ant, direction)) {
                return true;
            }
        }
        return false;
    }

    private String getTaskName() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            if (stackTrace[i].getClassName().startsWith(Task.class.getPackage().getName()))
                return stackTrace[i].getClassName();
        }
        return null; // should never happen
    }

    public Map<Tile, Move> getOrders() {
        return orders;
    }

    public Set<Mission> getMissions() {
        return missions;
    }

    public void addMission(Mission newMission) {
        Logger.debug(LogCategory.EXECUTE_MISSIONS, "New mission created: %s", newMission);
        missions.add(newMission);
    }

    public void issueOrders() {
        for (Move move : orders.values()) {
            if (move != null) {
                final String order = "o " + move.getTile().getRow() + " " + move.getTile().getCol() + " "
                        + move.getDirection().getSymbol();
                Logger.debug(LogCategory.ORDERS, "Issuing order: %s", order);
                System.out.println(order);
            }
        }
    }
}
