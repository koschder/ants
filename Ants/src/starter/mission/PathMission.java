package starter.mission;

import java.util.ArrayList;
import java.util.List;

import starter.Ant;
import starter.Ants;
import starter.Tile;

public abstract class PathMission extends Mission {

    public List<Tile> path = new ArrayList<Tile>();
    
    public PathMission(Ant ant, Ants ants) {
        super(ant, ants);
    }

}
