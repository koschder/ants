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

    public ExploreMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    /***
     * mission isn't valid if there is food near the ant or there is an emeny near the ant.
     */
    @Override
    protected boolean isSpecificMissionValid() {

        // // is the visible tile already explored?
        // if (Ants.getWorld().isVisible(path.get(path.size() - 1)))
        // return false;

        for (Ant ant : this.ants) {
            // TODO really abort the mission if only one ant found something more interesting?
            if (isSomethingInterestingNearby(ant)) {
                return false;
            }

        }
        return true;
    }

    private boolean isSomethingInterestingNearby(Ant ant) {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile());
        List<Ant> enemy = ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false);
        final boolean enemyIsMayor = enemy.size() > getAnts().size();
        return (foodNearby || enemyIsMayor);
    }
}
