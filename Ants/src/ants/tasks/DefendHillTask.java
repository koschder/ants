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
            int hillCount = 0;
            for (Tile h : Ants.getWorld().getMyHills()) {
                // with the statment
                // turn < minTurnToDefendMyHill + hillCount
                // we prevent, that alle defend hill missions are instanced in the same turn.
                // in the constructor of DefendHillMission there is a flood algorithm, witch is a bit expensive.
                if (hasMissonForHill(h) || turn < minTurnToDefendMyHill + hillCount)
                    continue;
                addMission(new DefendHillMission(h));
                hillCount++;
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
