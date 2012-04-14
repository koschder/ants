package starter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import starter.Logger.LogCategory;
import starter.mission.Mission;

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

    public boolean putOrder(Ant ant, Aim direction) {
        // Track all moves, prevent collisions
        Tile newLoc = Ants.getWorld().getTile(ant.getTile(), direction);
        if (Ants.getWorld().getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            Ants.getPopulation().addEmployedAnt(ant);
            return true;
        } else {
            return false;
        }
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
