package ants.missions;

import java.util.*;

import ants.entities.*;
import ants.state.*;
import api.entities.*;

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
        for (Ant ant : this.ants) {
            // TODO really abort the mission if only one ant found something more interesting?
            if (isSomethingInterestingNearby(ant))
                return false;
        }
        return true;
    }

    private boolean isSomethingInterestingNearby(Ant ant) {
        final boolean foodNearby = Ants.getWorld().isFoodNearby(ant.getTile());
        final boolean enemyInRange = !ant.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), false).isEmpty();
        return (!foodNearby && !enemyInRange);
    }

}
