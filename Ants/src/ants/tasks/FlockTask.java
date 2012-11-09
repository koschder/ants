package ants.tasks;

import ants.missions.*;
import ants.state.*;
import api.entities.*;

public class FlockTask extends BaseTask {
    @Override
    protected void doPerform() {
        Tile target = new Tile(24, 8);

        if (Ants.getPopulation().getMyUnemployedAnts().size() > 3) {
            addMission(new FlockMission(target, Ants.getPopulation().getMyUnemployedAnts()));
        }
    };
}
