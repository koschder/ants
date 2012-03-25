package starter.tasks;

import java.util.List;
import java.util.Map;

import starter.AStarSearchStrategy;
import starter.Aim;
import starter.Ants;
import starter.Logger;
import starter.SearchStrategy;
import starter.Tile;

public abstract class BaseTask implements Task {

    protected Ants ants;
    protected Map<Tile, Tile> orders;
    protected SearchStrategy search;

    protected boolean doMoveDirection(Tile antLoc, Aim direction) {
        // Track all moves, prevent collisions
        Tile newLoc = ants.getTile(antLoc, direction);
        if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            ants.issueOrder(antLoc, direction);
            orders.put(newLoc, antLoc);
            Logger.log("%1$s: Moving ant from %2$s to %3$s", getClass().getSimpleName(), antLoc, newLoc);
            return true;
        } else {
            return false;
        }
    }

    protected boolean doMoveLocation(Tile antLoc, Tile destLoc) {
        List<Tile> path = search.bestPath(antLoc, destLoc);
        // Track targets to prevent 2 ants to the same location
        List<Aim> directions = ants.getDirections(antLoc, path != null ? path.get(0) : destLoc);
        for (Aim direction : directions) {
            if (doMoveDirection(antLoc, direction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setup(Ants ants, Map<Tile, Tile> orders) {
        this.ants = ants;
        this.orders = orders;
        this.search = new AStarSearchStrategy(ants, orders);
    }
}