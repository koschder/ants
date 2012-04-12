package starter.mission;

import java.util.List;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Tile;

public class AttackMission extends PathMission {

    public AttackMission(Ant ant, Ants ants, List<Tile> path) {
        super(ant, ants, path);
    }

    @Override
    public void perform() {
        if (path == null)
            return;
        Tile nextStep = path.remove(0);

        Aim aim = ant.getTile().directionTo(nextStep);
        Logger.log("Go to: %s direction is %s", nextStep, aim);
        if (putMissionOrder(ant, aim)) {
        } else {
            // TODO what else
            Logger.log("no move done for Mission %s ", this);
        }
        return;

    }

    @Override
    public boolean IsMissionComplete() {
        return (path == null || path.size() == 0);
    }

    @Override
    protected boolean IsSpecificMissionValid() {
        // TODO check if enemy is still there
        return true;
    }

}
