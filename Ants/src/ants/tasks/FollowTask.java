package ants.tasks;

import java.util.Iterator;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.missions.FollowMission;
import ants.missions.Mission;
import ants.state.Ants;

public class FollowTask extends BaseTask {

    private final int MAX_DISTANCE_TO_START_FOLLOW = 6;

    @Override
    public void perform() {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            final Tile antLoc = ant.getTile();

            // remove any tiles that can be seen, run each turn
            for (Iterator<Mission> locIter = Ants.getOrders().getMissions().iterator(); locIter.hasNext();) {
                Mission m = locIter.next();
                Ant a = m.getAnt();
                int distance = antLoc.manhattanDistanceTo(a.getTile());
                if (MAX_DISTANCE_TO_START_FOLLOW > distance) {
                    Ants.getOrders().addMission(new FollowMission(ant, m));
                    return;
                }
            }

        }
    }
}
