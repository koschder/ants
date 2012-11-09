package ants.tasks;

import java.util.List;

import pathfinder.PathFinder;
import ants.missions.MulitAntPathMission;
import ants.state.Ants;
import api.entities.Tile;

public class ConcentrateTask extends BaseTask {
    @Override
    public void doPerform() {

        Tile hill = new Tile(38, 33);
        Tile target = new Tile(24, 8);

        List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, hill, target);

        if (Ants.getAnts().getTurn() == 0) {
            Tile tp = new Tile(hill.getRow(), hill.getCol());
            addMission(new MulitAntPathMission(null, path));
        }
    }
    // @Override
    // public void doPerform() {
    //
    // Tile hill = new Tile(32, 8);
    //
    // if (Ants.getAnts().getTurn() == 0) {
    // Tile tp = new Tile(hill.getRow(), hill.getCol());
    // addMission(new ConcentrateMission(tp, 8));
    // }
    //
    // for (Mission m : Ants.getOrders().getMissions())
    // if (m instanceof ConcentrateMission) {
    // ((ConcentrateMission) m).gatherAnts();
    // }
    // }
}
