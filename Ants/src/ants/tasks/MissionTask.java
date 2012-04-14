package ants.tasks;

import java.util.Iterator;
import java.util.Set;

import ants.missions.Mission;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class MissionTask extends BaseTask {

    @Override
    public void setup() {
        super.setup();
        for (Mission m : Ants.getOrders().getMissions()) {
            m.getAnt().setup();
        }
    }

    @Override
    public void perform() {

        Set<Mission> missions = Ants.getOrders().getMissions();
        Logger.debug(LogCategory.EXECUTE_MISSIONS, "Current mission count: %s", missions.size());
        for (Iterator<Mission> it = missions.iterator(); it.hasNext();) {
            Mission mission = it.next();
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "mission: %s", mission);
            if (mission.isValid()) {
                mission.execute();
                Logger.debug(LogCategory.EXECUTE_MISSIONS, "Mission performed: %s", mission);
                if (mission.isComplete()) {
                    it.remove();
                }
            } else {
                Logger.debug(LogCategory.EXECUTE_MISSIONS, "Mission not vaild: %s. Mission is removed.", mission);
                it.remove();
            }
        }

    }
}
