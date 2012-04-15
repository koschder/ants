package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.state.Ants;

public class AttackHillMission extends PathMission {

    Tile hill;

    public AttackHillMission(Ant ant, List<Tile> path) {
        super(ant, path);
        hill = path.get(path.size() - 1);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(hill))
            return false;
        return true;
    }

}
