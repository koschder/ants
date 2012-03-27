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
           if(mission.IsMissionValid()){
               mission.proceedMission();
               Logger.log("Mission proceeded: %s",mission);
               if(mission.IsMissionComplete()){
                   missions.remove(mission);
               }
           }
           missions.remove(mission);
       }

    }

}
