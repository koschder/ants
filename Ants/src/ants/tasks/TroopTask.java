package ants.tasks;

import ants.missions.Mission;
import ants.missions.TroopMission;
import ants.state.Ants;
import api.entities.Tile;

public class TroopTask extends BaseTask {

    @Override
    public void doPerform() {

        Tile hill = new Tile(32, 8);

        if (Ants.getAnts().getTurn() == 1) {
            Tile tp = new Tile(hill.getRow(), hill.getCol());
            addMission(new TroopMission(tp, 8));
        }

        for (Mission m : Ants.getOrders().getMissions())
            if (m instanceof TroopMission) {
                ((TroopMission) m).gatherAnts();
            }
    }
}
