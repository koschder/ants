package starter.tasks;

import java.util.Iterator;
import java.util.Set;

import starter.Ants;
import starter.Logger;
import starter.Logger.LogCategory;
import starter.mission.Mission;

public class MissionTask extends BaseTask {

    @Override
    public void setup(Ants ants) {
        super.setup(ants);
        for (Mission m : ants.getMissions()) {
            m.getAnt().setup();
        }
    }

    @Override
    public void perform() {

        Set<Mission> missions = ants.getMissions();
        Logger.log(LogCategory.EXECUTE_MISSIONS, "Current mission count: %s", missions.size());
        for (Iterator<Mission> it = missions.iterator(); it.hasNext();) {
            Mission mission = it.next();
            Logger.log(LogCategory.EXECUTE_MISSIONS, "mission: %s", mission);
            if (mission.isMissionValid()) {
                mission.perform();
                Logger.log(LogCategory.EXECUTE_MISSIONS, "Mission performed: %s", mission);
                if (mission.IsMissionComplete()) {
                    it.remove();
                }
            } else {
                Logger.log(LogCategory.EXECUTE_MISSIONS, "Mission not vaild: %s. Mission is removed.", mission);
                it.remove();
            }
        }

    }
}
