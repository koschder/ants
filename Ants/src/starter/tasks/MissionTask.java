package starter.tasks;

import java.util.Iterator;
import java.util.Set;

import starter.Ant;
import starter.Logger;
import starter.mission.Mission;

public class MissionTask extends BaseTask {

    @Override
    public void perform() {
       
       Set<Mission> missions = ants.getMissions();
       Logger.log("Current mission count: %s", missions.size());
       for (Iterator<Mission> it = missions.iterator(); it.hasNext();) {
           Mission mission = it.next();
           Logger.log("mission: %s", mission);
           if(mission.IsMissionValid()){
               it.remove();
               Logger.log("Mission proceeded: %s",mission);
               if(mission.IsMissionComplete()){
                   missions.remove(mission);
               }
           }else{
               Logger.log("Mission not vaild: %s. Mission is removed.",mission);
               it.remove();
           }
       }

    }

}
