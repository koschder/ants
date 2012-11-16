package ants.missions;

import java.util.*;

import ants.entities.*;
import ants.state.*;
import api.entities.*;

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
    }

    /***
     * is valid as long the food is on the map
     */
    @Override
    public String isSpecificMissionValid() {
        // if food to gather isn't there the mission is not valid.
        if (!Ants.getWorld().getIlk(food).isFood())
            return "The cake is a lie";
        // TODO other checks here
        return null;
    }

    @Override
    public String toString() {
        String direction = "TODO"; // ((path.size() > 0) ? ant.getTile().directionTo(path.get(0)).toString() : "-");
        return "GatherFoodMission: [ant=" + getAnt() + ", target food=" + food + ", Next direction:=" + direction
                + ", Path:=" + getPathString() + ", IsMissionComplete()=" + isComplete() + "]";
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>Food: " + food;
    }
}
