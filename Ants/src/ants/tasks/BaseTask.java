package ants.tasks;

import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.search.AStarSearchStrategy;
import ants.search.SearchStrategy;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;


public abstract class BaseTask implements Task {

    protected SearchStrategy search;

    protected boolean doMoveDirection(Ant ant, Aim direction) {
        if (Ants.getAnts().putOrder(ant, direction)) {
            Logger.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "Task: %s ant: %s", getClass().getSimpleName(),
                    ant);
            Logger.debug(LogCategory.EXECUTE_TASKS, "%1$s: Moving ant from %2$s to %3$s", getClass().getSimpleName(),
                    ant.getTile(), ant.getNextTile());
            return true;
        } else {
            return false;
        }
    }

    protected boolean doMoveLocation(Ant ant, Tile destLoc) {

        try {
            List<Tile> path = search.bestPath(ant.getTile(), destLoc);
            if (path == null)
                return false;
            List<Aim> directions = Ants.getWorld().getDirections(ant.getTile(), path != null ? path.get(0) : destLoc);
            for (Aim direction : directions) {
                if (doMoveDirection(ant, direction)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.exception("ant: %s tile: %s", ex, ant, destLoc);
        }
        return false;
    }

    @Override
    public void setup() {
        this.search = new AStarSearchStrategy(6);
    }
}