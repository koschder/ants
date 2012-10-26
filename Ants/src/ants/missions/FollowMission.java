package ants.missions;

import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import api.entities.Tile;

/***
 * This mission is used to let an ant follow an other ant which has already a mission.
 * 
 * @author kases1,kustl1
 * 
 */
public class FollowMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOLLOW);
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

    /***
     * as long as the master mission is valid this mission is vaild to. if the ant of the master mission is to far away
     * this mission is invalid
     */
    @Override
    protected boolean isSpecificMissionValid() {
        if (!mastermission.isValid())
            return false;
        Move m = mastermission.getLastMove();
        if (m == null)
            return false;

        int distance = m.getTile().manhattanDistanceTo(ant.getTile());
        if (distance > MAX_DISTANCE_TO_MASTER) {
            LOGGER.debug("FollowMission cancelled, master is to far away : %s max is: %s)", distance,
                    MAX_DISTANCE_TO_MASTER);
            return false;

        }
        return true;
    }

    @Override
    /***
     * move towards the ant of the mastermission
     */
    public void execute() {

        Move m = mastermission.getLastMove();
        if (m == null || m.getTile().equals(ant.getTile())) // no move done yet, wait to the next round;
            return;
        List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.Simple, ant.getTile(), m.getTile());
        if (path == null)
            abandonMission();
        else
            putMissionOrder(ant, path.get(0)); // ant.getTile().directionTo(path.get(0)));
    }

    @Override
    public String toString() {
        return "FollowMission [mastermission=" + mastermission + "]";
    }

}
