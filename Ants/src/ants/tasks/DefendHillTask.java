package ants.tasks;

import ants.missions.DefendHillMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * This task is responsible for creating DefendHillMissions for our hills.
 * 
 * @author kases1, kustl1
 * 
 */
public class DefendHillTask extends BaseTask {

    @Override
    public void doPerform() {

        for (Tile h : Ants.getWorld().getMyHills()) {
            if (hasMissonForHill(h))
                continue;
            addMission(new DefendHillMission(h));
        }

    }

    private boolean hasMissonForHill(Tile h) {
        for (Mission m : Ants.getOrders().getMissions()) {
            if (m instanceof DefendHillMission)
                if (((DefendHillMission) m).getHill().equals(h))
                    return true;

        }
        return false;
    }

    @Override
    public Type getType() {
        return Type.DEFEND_HILL;
    }
}
