package ants.missions;

import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class GatherFoodMission extends PathMission {

    Tile food = path.get(path.size() - 1);

    public GatherFoodMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    @Override
    public boolean isComplete() {
        // only the food tile is in path list (but for gather the food we don't need to move to this field)
        return (path == null || path.size() == 0);
    }

    @Override
    public boolean isSpecificMissionValid() {
        // if food to gather isn't there the mission is not vaild.
        if (!Ants.getWorld().getIlk(food).isFood())
            return false;
        // TODO other checks here
        return true;
    }

    @Override
    public String toString() {
        String direction = ((path.size() > 0) ? ant.getTile().directionTo(path.get(0)).toString() : "-");
        return "GatherFoodMission: [ant=" + ant.getTile() + ", target food=" + food + ", Next direction:=" + direction
                + ", Path:=" + getPathString() + ", IsMissionComplete()=" + isComplete() + "]";
    }

    @Override
    public void execute() {
        if (path == null)
            return;
        Tile nextStep = path.remove(0);

        Aim aim = ant.getTile().directionTo(nextStep);
        Logger.debug(LogCategory.FOOD, "Go to: %s direction is %s", nextStep, aim);
        if (putMissionOrder(ant, aim)) {
        } else {
            // TODO what else
            Logger.debug(LogCategory.FOOD, "no move done for Mission %s ", this);
        }
        return;
    }
}
