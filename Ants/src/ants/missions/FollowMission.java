package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Move;
import ants.entities.Tile;
import ants.search.PathFinder;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

/***
 * This mission is used to let an ant follow an other ant which has already a mission.
 * 
 * @author kaeserst
 * 
 */
public class FollowMission extends BaseMission {

    private Mission mastermission;
    private final int MAX_DISTANCE_TO_MASTER = 6;

    public FollowMission(Ant ant, Mission m) {
        super(ant);
        mastermission = m;
    }

    @Override
    public boolean isComplete() {
        return (mastermission == null || mastermission.isComplete());
    }

    @Override
    protected boolean isSpecificMissionValid() {

        Move m = mastermission.getLastMove();
        if (m == null) // no move done yet, wait to the next round;
            return true;

        int distance = m.getTile().manhattanDistanceTo(ant.getTile());
        if (distance > MAX_DISTANCE_TO_MASTER) {
            Logger.debug(LogCategory.FOLLOW, "FollowMission cancelled, master is to far away : %s max is: %s)",
                    distance, MAX_DISTANCE_TO_MASTER);
            return false;

        }
        return true;
    }

    @Override
    public void execute() {

        Move m = mastermission.getLastMove();
        if (m == null || m.getTile().equals(ant.getTile())) // no move done yet, wait to the next round;
            return;
        List<Tile> path = PathFinder.bestPath(PathFinder.SIMPLE, ant.getTile(), m.getTile());
        if (path == null)
            abandonMission();
        else
            Ants.getOrders().moveToNextTile(ant, path);
    }

    @Override
    public String toString() {
        return "FollowMission [mastermission=" + mastermission + "]";
    }

}
