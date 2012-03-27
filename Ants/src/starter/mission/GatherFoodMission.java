package starter.mission;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Move;
import starter.Tile;

public class GatherFoodMission extends PathMission {

    Tile food = path.get(path.size()-1);

    public GatherFoodMission(Ant ant, Ants ants) {
        super(ant, ants);
    }

    @Override
    public boolean IsMissionComplete() {
        // only the food tile is in path list (but for gather the food we don't need to move to this field)
        return (path.size() == 1);
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
        return "GatherFoodMission: [target food=" + food + ", ant=" + ant + ", IsMissionComplete()=" + IsMissionComplete()
                + "]";
    }

    @Override
    public void proceedMission() {
        
        Tile nextStep = path.remove(0);
        //todo right direction
        if(ants.putOrder(ant, Aim.NORTH)){
            return;
        }
        //todo what else;
        return;
    }
}
