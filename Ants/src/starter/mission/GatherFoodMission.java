package starter.mission;

import java.util.List;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Move;
import starter.Tile;

public class GatherFoodMission extends PathMission {

    Tile food = path.get(path.size()-1);

    public GatherFoodMission(Ant ant, Ants ants,List<Tile> path) {
        super(ant, ants,path);
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
        return "GatherFoodMission: [ant=" + ant.getTile() + ", target food=" + food + ", Next direction:="+ant.getTile().directionTo(path.get(0))+", Path:="+GetPathString()+", IsMissionComplete()=" + IsMissionComplete()
                + "]";
    }

    @Override
    public void proceedMission() {
        if(path==null)
            return;       
        Tile nextStep = path.remove(0);
        
        Aim aim = ant.getTile().directionTo(nextStep);
        //Logger.log("Go to: %s direction is %s", nextStep,aim);
        if(ants.putOrder(ant,aim)){
            //TODO wird in putorder gemacht, aber für ant nicht übernommen?
            ant.setNextTile(ants.getTile(ant.getTile(), aim));
            return;
        }
        //TODO what else;
        return;
    }
}
