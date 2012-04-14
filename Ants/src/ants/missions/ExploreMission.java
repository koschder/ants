package ants.missions;

import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class ExploreMission extends PathMission {

    public ExploreMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    @Override
    public void execute() {
        if (path == null)
            return;
        Tile nextStep = path.remove(0);

        Aim aim = ant.getTile().directionTo(nextStep);
        Logger.debug(LogCategory.EXPLORE, "Go to: %s direction is %s", nextStep, aim);
        if (putMissionOrder(ant, aim)) {
        } else {
            // TODO what else
            Logger.debug(LogCategory.EXPLORE, "no move done for Mission %s ", this);
        }
        return;
    }

    @Override
    public boolean isComplete() {
        return (path == null || path.size() == 0);
    }

    @Override
    protected boolean IsSpecificMissionValid() {
        return true;
    }

}
