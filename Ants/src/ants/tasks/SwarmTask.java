package ants.tasks;

import java.util.List;

import pathfinder.PathFinder;
import ants.missions.SwarmPathMission;
import ants.state.Ants;
import api.entities.Tile;

public class SwarmTask extends BaseTask {
    @Override
    public void doPerform() {

        if (Ants.getAnts().getTurn() == 1) {
            Tile hill = new Tile(38, 59);
            Tile target = new Tile(25, 33);
            List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, hill, target);
            SwarmPathMission m = new SwarmPathMission(null, path, 15);
            addMission(m);
        }
    }

    @Override
    public Type getType() {
        return Type.SWARMPATH;
    }
}
