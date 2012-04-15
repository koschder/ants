package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.state.Ants;

public class ExploreMission extends PathMission {

    public ExploreMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile());
        final boolean enemyInRange = !ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false).isEmpty();
        return (!foodNearby && !enemyInRange);
    }

}
