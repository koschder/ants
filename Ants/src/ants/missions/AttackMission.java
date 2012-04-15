package ants.missions;

import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class AttackMission extends PathMission {

    public AttackMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    @Override
    public void execute() {
        if (path == null)
            return;
        Tile nextStep = path.remove(0);

        Aim aim = ant.getTile().directionTo(nextStep);
        Logger.debug(LogCategory.COMBAT, "Go to: %s direction is %s", nextStep, aim);
        if (!putMissionOrder(ant, aim))
            abandonMission();
        return;

    }

    @Override
    public boolean isComplete() {
        return (path == null || path.size() == 0);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        // TODO check if enemy is still there
        return true;
    }

}
