package ants.tasks;

import java.util.Collection;

import ants.entities.Ant;
import ants.missions.FlockMission;
import ants.state.Ants;
import api.entities.Tile;

public class FlockTask extends BaseTask {
    @Override
    protected void doPerform() {
        Tile target = new Tile(24, 8);

        final Collection<Ant> ants = Ants.getPopulation().getMyUnemployedAnts();
        if (ants.size() > 3) {
            addMission(new FlockMission(target, ants), ants.toArray(new Ant[] {}));
        }
    };
}
