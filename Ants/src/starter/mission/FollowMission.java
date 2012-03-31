package starter.mission;

import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Move;

/***
 * This mission is used to let an ant follow an other ant which has already a mission.
 * 
 * @author kaeserst
 * 
 */
public class FollowMission extends Mission {

    private Mission mastermission;
    private final int MAX_DISTANCE_TO_MASTER = 6;

    public FollowMission(Ant ant, Ants ants, Mission m) {
        super(ant, ants);
        mastermission = m;
    }

    @Override
    public boolean IsMissionComplete() {
        return (mastermission == null || mastermission.IsMissionComplete());
    }

    @Override
    protected boolean IsSpecificMissionValid() {

        Move m = mastermission.getLastMove();
        if (m == null) // no move done yet, wait to the next round;
            return true;

        int distance = m.getTile().manhattanDistanceTo(ant.getTile());
        if (distance > MAX_DISTANCE_TO_MASTER) {
            Logger.log("FollowMission cancelled, master is to far away : %s max is: %s)", distance,
                    MAX_DISTANCE_TO_MASTER);
            return false;

        }
        return true;
    }

    @Override
    public void perform() {

        Move m = mastermission.getLastMove();
        if (m == null) // no move done yet, wait to the next round;
            return;
        doMoveLocation(ant, m.getTile());
    }

    @Override
    public String toString() {
        return "FollowMission [mastermission=" + mastermission + "]";
    }

}
