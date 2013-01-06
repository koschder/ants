package ants.tasks;

import ants.missions.ConcentrateMission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * 
 * @author kases1, kustl1
 * @deprecated Did not work as hoped, was abandoned
 */
public class ConcentrateTask extends BaseTask {

    @Override
    public void doPerform() {

        Tile hill = new Tile(32, 8);
        if (Ants.getAnts().getTurn() == 0) {
            Tile tp = new Tile(hill.getRow(), hill.getCol());
            addMission(new ConcentrateMission(tp, 8, 25));
        }
    }

    @Override
    public Type getType() {
        return Type.CONCENTRATE_ANTS;
    }
}
