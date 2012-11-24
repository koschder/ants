package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Tile;

/***
 * Mission for gathering a food tile
 * 
 * @author kustl1, kases1
 * 
 */
public class GatherFoodMission extends PathMission {

    Tile food;

    public GatherFoodMission(Ant ant, List<Tile> path) {

        super(ant, path);
        food = path.get(path.size() - 1);
        // remove the tile where the food, we don't have to move there.
        // if (path.size() > 1)
        // ant.setPath(path.subList(0, path.size() - 1));

    }

    /***
     * is valid as long the food is on the map
     */
    @Override
    public String isSpecificMissionValid() {
        // if food to gather isn't there the mission is not valid.
        if (!Ants.getWorld().getIlk(food).isFood())
            return "The cake is a lie";

        for (Ant ant : ants) {
            String abortReason = checkEnviroment(ant, false, false, true);
            if (abortReason.length() > 0) {
                return "Found something [" + abortReason + "] more interesting";
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "GatherFoodMission: [ant=" + getAnt() + ", target food=" + food + ", Path:=" + getPathString()
                + ", IsMissionComplete()=" + isComplete() + "]";
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>Food: " + food;
    }

    @Override
    public Type getTaskType() {
        return Type.GATHER_FOOD;
    }
}
