package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.tasks.Task.Type;
import api.entities.Tile;

/***
 * Mission for exploring the world
 * 
 * @author kaeserst
 * 
 */
public class ExploreMission extends PathMission {

    public ExploreMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    /***
     * mission isn't valid if there is food near the ant or there is an emeny near the ant.
     */
    @Override
    protected String isSpecificMissionValid() {

        for (Ant ant : this.ants) {
            // TODO really abort the mission if only one ant found something more interesting?
            String abortReason = checkEnviroment(ant, true, false, true);
            if (abortReason.length() > 0) {
                return "Found something [" + abortReason + "] more interesting";
            }

        }
        return null;
    }

    @Override
    public Type getTaskType() {
        return Type.EXPLORE;
    }
}
