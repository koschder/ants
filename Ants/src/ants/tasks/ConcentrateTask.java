package ants.tasks;

import ants.missions.ConcentrateMission;
import ants.state.Ants;
import api.entities.Tile;

public class ConcentrateTask extends BaseTask {

    @Override
    public void doPerform() {

        Tile hill = new Tile(32, 8);
        if (Ants.getAnts().getTurn() == 0) {
            Tile tp = new Tile(hill.getRow(), hill.getCol());
            addMission(new ConcentrateMission(tp, 8));
        }
    }
}
