package ants.missions;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public abstract class PathMission extends BaseMission {

    public List<Tile> path = new ArrayList<Tile>();

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        this.path = path;
    }

    public String getPathString() {
        if (path == null)
            return "path is null";
        String pathString = "";
        for (Tile t : path) {
            pathString += t + ",";
        }
        return pathString;
    }

    @Override
    public boolean isComplete() {
        return path == null || path.isEmpty();
    }

    @Override
    public void execute() {
        if (!moveToNextTile())
            abandonMission();
    }

    protected boolean moveToNextTile() {
        if (path == null)
            return false;

        Tile nextStep = path.remove(0);
        Aim aim = ant.getTile().directionTo(nextStep);

        if (putMissionOrder(ant, aim)) {
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "Go to: %s direction is %s", nextStep, aim);
            return true;
        }
        return false;
    }
}
