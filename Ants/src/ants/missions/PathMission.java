package ants.missions;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Tile;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

/***
 * this mission is implemented to follow a path defined while creating the class
 * @author kases1, kustl1
 *
 */
public abstract class PathMission extends BaseMission {

    public List<Tile> path = new ArrayList<Tile>();

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        this.path = path;
    }

    /***
     * 
     * @return the whole path as a string.
     */
    public String getPathString() {
        if (path == null)
            return "path is null";
        String pathString = "";
        Tile previos = null;
        String aims = "";
        for (Tile t : path) {
            pathString += t + ",";
            if (previos != null) {
                aims += previos.directionTo(t).name().charAt(0);
            }
            previos = t;
        }
        return pathString + "\n Aims: " + aims + " \n";
    }

    @Override
    public boolean isComplete() {
        return path == null || path.isEmpty();
    }

    @Override
    public void execute() {
        if (!moveToNextTile())
            abandonMission();
    }

    /***
     * puts the order in the order list where the ant has to go and remove this path piece.
     * @return true if order is put successful.
     */
    protected boolean moveToNextTile() {
        if (path == null)
            return false;

        Tile nextStep = path.remove(0);
        Aim aim = ant.getTile().directionTo(nextStep);

        if (putMissionOrder(ant, aim)) {
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "Go to: %s direction is %s", nextStep, aim);
            return true;
        }
        return false;
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>Path: " + getPathString();
    }
}
