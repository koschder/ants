package ants.tasks;

import ants.missions.DefendHillMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

public class DefendHillTask extends BaseTask {

    @Override
    public void doPerform() {

        int minTurnToDefendMyHill = 10;
        int minMyHillToDefend = 999;
        int turn = Ants.getAnts().getTurn();
        int myHills = Ants.getWorld().getMyHills().size();
        if (turn > minTurnToDefendMyHill && myHills < minMyHillToDefend) {
            for (Tile h : Ants.getWorld().getMyHills()) {
                if (hasMissonForHill(h))
                    continue;
                addMission(new DefendHillMission(h));
            }
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
}
