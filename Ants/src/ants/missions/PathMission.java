package ants.missions;

import java.util.List;

import ants.entities.Ant;
import api.entities.Tile;

/**
 * This mission is implemented to follow a path defined while creating the class
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class PathMission extends BaseMission {

    /**
     * Default constructor
     * 
     * @param ant
     * @param path
     */
    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        ant.setPath(path);
    }

    /**
     * 
     * @return the ant on this mission
     */
    protected Ant getAnt() {
        return ants.size() > 0 ? ants.get(0) : null;
    }

    /**
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

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + "<br/>";
    }

}
