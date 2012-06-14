package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.state.Ants;

/***
 * Mission for exploring the world
 * @author kaeserst
 *
 */
public class ExploreMission extends PathMission {

    public ExploreMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    /***
     * mission isn't valid if there is food near the ant or there is an emeny near the ant.
     */
    @Override
    protected boolean isSpecificMissionValid() {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile());
        final boolean enemyInRange = !ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false).isEmpty();
        return (!foodNearby && !enemyInRange);
    }

}
