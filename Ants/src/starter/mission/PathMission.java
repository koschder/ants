package starter.mission;

import java.util.ArrayList;
import java.util.List;

import starter.Ant;
import starter.Tile;

public abstract class PathMission extends Mission {

    public List<Tile> path = new ArrayList<Tile>();

    public PathMission(Ant ant, List<Tile> path) {
        super(ant);
        this.path = path;
    }

    public String getPathString() {
        if (path == null)
            return "path is null";
        String pathString = "";
        for (Tile t : path) {
            pathString += t + ",";
        }
        return pathString;
    }

}
