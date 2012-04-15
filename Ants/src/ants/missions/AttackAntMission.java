package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;

public class AttackAntMission extends PathMission {

    public AttackAntMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        // TODO check if enemy is still there
        return true;
    }

}
