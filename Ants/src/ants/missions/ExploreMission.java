package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;

/***
 * Mission for exploring the world
 * 
 * @author kaeserst
 * 
 */
public class ExploreMission extends PathMission {

    public ExploreMission(List<Tile> path) {
        super(path);
    }

    /***
     * mission isn't valid if there is food near the ant or there is an emeny near the ant.
     */
    @Override
    protected boolean isSpecificMissionValid() {
        for (Ant ant : this.getAnts()) {
            // TODO really abort the mission if only one ant found something more interesting?
            if (isSomethingInterestingNearby(ant)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSomethingInterestingNearby(Ant ant) {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile());
        final boolean enemyInRange = !ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false).isEmpty();
        return (foodNearby || enemyInRange);
    }

}
