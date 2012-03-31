package starter.tasks;

import java.util.Iterator;

import starter.Ant;
import starter.Tile;
import starter.mission.FollowMission;
import starter.mission.Mission;

public class FollowTask extends BaseTask {

    private final int MAX_DISTANCE_TO_START_FOLLOW = 6;

    @Override
    public void perform() {
        for (Ant ant : ants.getMyUnemployedAnts()) {
            final Tile antLoc = ant.getTile();

            // remove any tiles that can be seen, run each turn
            for (Iterator<Mission> locIter = ants.getMissions().iterator(); locIter.hasNext();) {
                Mission m = locIter.next();
                Ant a = m.getAnt();
                int distance = antLoc.manhattanDistanceTo(a.getTile());
                if (MAX_DISTANCE_TO_START_FOLLOW > distance) {
                    Mission mFollow = new FollowMission(ant, ants, m);
                    ants.addMission(mFollow);
                    mFollow.perform();
                    return;
                }
            }

        }
    }
}
