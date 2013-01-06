package ants.tasks;

import ants.missions.FlockMission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * 
 * @author kases1, kustl1
 * @deprecated Did not work as hoped, was abandoned
 */
public class FlockTask extends BaseTask {
    @Override
    protected void doPerform() {
        Tile target = new Tile(24, 8);

        if (Ants.getPopulation().getMyUnemployedAnts().size() > 3) {
            addMission(new FlockMission(target, Ants.getPopulation().getMyUnemployedAnts()));
        }
    };

    @Override
    public Type getType() {
        return Type.FLOCK;
    }
}
