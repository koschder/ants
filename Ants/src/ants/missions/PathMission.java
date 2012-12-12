package ants.missions;

import java.util.List;

import ants.entities.Ant;
import api.entities.Tile;

/***
 * this mission is implemented to follow a path defined while creating the class
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class PathMission extends BaseMission {

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        ant.setPath(path);
    }

    protected Ant getAnt() {
        return ants.size() > 0 ? ants.get(0) : null;
    }

    /***
     * 
     * @return the whole path as a string.
     */
    public String getPathString() {
        if (getAnt() == null)
            return "";
        if (getAnt().hasPath())
            return getAnt().getPath().toString();
        return "path is null";
    }

    @Override
    public boolean isComplete() {
        return ants.size() == 0 || !getAnt().hasPath();
    }

    @Override
    public void execute() {
        if (!moveToNextTileOnPath(getAnt()))
            abandonMission();
    }

    /***
     * puts the order in the order list where the ant has to go and remove this path piece.
     * 
     * @return true if order is put successful.
     */

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>";
    }

}
