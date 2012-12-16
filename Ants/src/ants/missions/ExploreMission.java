package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
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

    @Override
    public void execute() {

        if (getAnt().hasPath()) {
            Tile t = getAnt().getPath().get(0);
            int safety = Ants.getInfluenceMap().getSafety(t);
            if (safety < -20) {
                if (getAnt().getTurnsWaited() > 3) // too long waited.
                    abandonMission();
                else
                    LiveInfo.liveInfo(Ants.getAnts().getTurn(), getAnt().getTile(),
                            "ExploreMission stay. (safety is: %s, turnsWaited: %s)", safety, getAnt().getTurnsWaited());
                return;
            }
        }

        if (!moveToNextTileOnPath(getAnt()))
            abandonMission();
    }

    /***
     * mission isn't valid if there is food near the ant or there is an emeny near the ant.
     */
    @Override
    protected String isSpecificMissionValid() {

        for (Ant ant : this.ants) {
            // TODO really abort the mission if only one ant found something more interesting?
            String abortReason = checkEnviroment(ant, true, true, true);
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
