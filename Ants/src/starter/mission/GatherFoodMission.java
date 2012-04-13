package starter.mission;

import java.util.List;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Logger.LogCategory;
import starter.Tile;

public class GatherFoodMission extends PathMission {

    Tile food = path.get(path.size() - 1);

    public GatherFoodMission(Ant ant, Ants ants, List<Tile> path) {
        super(ant, ants, path);
    }

    @Override
    public boolean IsMissionComplete() {
        // only the food tile is in path list (but for gather the food we don't need to move to this field)
        return (path == null || path.size() == 0);
    }

    @Override
    public boolean IsSpecificMissionValid() {
        // if food to gather isn't there the mission is not vaild.
        if (!ants.getIlk(food).isFood())
            return false;
        // TODO other checks here
        return true;
    }

    @Override
    public String toString() {
        String direction = ((path.size() > 0) ? ant.getTile().directionTo(path.get(0)).toString() : "-");
        return "GatherFoodMission: [ant=" + ant.getTile() + ", target food=" + food + ", Next direction:=" + direction
                + ", Path:=" + getPathString() + ", IsMissionComplete()=" + IsMissionComplete() + "]";
    }

    @Override
    public void perform() {
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
