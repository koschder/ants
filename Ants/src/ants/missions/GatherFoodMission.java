package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.state.Ants;

public class GatherFoodMission extends PathMission {

    Tile food;

    public GatherFoodMission(Ant ant, List<Tile> path) {
        super(ant, path);
        food = path.get(path.size() - 1);
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
}
