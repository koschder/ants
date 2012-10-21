package ants.tasks;

import java.util.Iterator;

import ants.entities.Ant;
import ants.missions.FollowMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * This task orders our ants that don't have anything better to do to follow another ant. Should be executed next to
 * last in the chain of tasks.
 * 
 * @author kases1,kustl1
 * 
 */
public class FollowTask extends BaseTask {

    private final int MAX_DISTANCE_TO_START_FOLLOW = 6;

    @Override
    public void doPerform() {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            final Tile antLoc = ant.getTile();

            for (Iterator<Mission> locIter = Ants.getOrders().getMissions().iterator(); locIter.hasNext();) {
                Mission m = locIter.next();
                Ant a = m.getAnt();
                int distance = antLoc.manhattanDistanceTo(a.getTile());
                if (MAX_DISTANCE_TO_START_FOLLOW > distance) {
                    addMission(new FollowMission(ant, m));
                    break;
                }
            }

        }
    }
}
