package starter.mission;

import java.util.List;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Logger.LogCategory;
import starter.Tile;

public class ExploreMission extends PathMission {

    public ExploreMission(Ant ant, Ants ants, List<Tile> path) {
        super(ant, ants, path);
    }

    @Override
    public void perform() {
        if (path == null)
            return;
        Tile nextStep = path.remove(0);

        Aim aim = ant.getTile().directionTo(nextStep);
        Logger.log(LogCategory.EXPLORE, "Go to: %s direction is %s", nextStep, aim);
        if (putMissionOrder(ant, aim)) {
        } else {
            // TODO what else
            Logger.log(LogCategory.EXPLORE, "no move done for Mission %s ", this);
        }
        return;
    }

    @Override
    public boolean IsMissionComplete() {
        return (path == null || path.size() == 0);
    }

    @Override
    protected boolean IsSpecificMissionValid() {
        return true;
    }

}
