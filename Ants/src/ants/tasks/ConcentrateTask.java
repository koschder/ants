package ants.tasks;

import ants.missions.*;
import ants.state.*;
import api.entities.*;

public class ConcentrateTask extends BaseTask {

    @Override
    public void doPerform() {

        Tile hill = new Tile(32, 8);
        if (Ants.getAnts().getTurn() == 0) {
            Tile tp = new Tile(hill.getRow(), hill.getCol());
            addMission(new ConcentrateMission(tp, 8, 25));
        }
    }
}
