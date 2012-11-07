package ants.missions;

import java.util.*;

import ants.entities.*;
import ants.state.*;
import api.entities.*;

/***
 * this mission is implemented to follow a path defined while creating the class
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class PathMission extends BaseMission {

    // private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    public List<Tile> path = new ArrayList<Tile>();

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        setPath(path);
    }

    private void setPath(List<Tile> path) {
        if (path.get(0).equals(getAnt().getTile()))
            path.remove(0);
        this.path = path;

    }

    protected Ant getAnt() {
        return ants.get(0);
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
                aims += Ants.getWorld().getDirections(previos, t).get(0).getSymbol();
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
     * 
     * @return true if order is put successful.
     */
    protected boolean moveToNextTile() {
        if (path == null)
            return false;

        Tile nextStep = path.remove(0);
        // Aim aim = ant.getTile().directionTo(nextStep);
        // Aim aim = ant.getTile().directionTo(nextStep);

        if (putMissionOrder(getAnt(), nextStep)) {
            return true;
        }
        return false;
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>Path: " + getPathString();
    }
}
