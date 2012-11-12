package ants.missions;

import java.util.List;

import ants.state.Ants;
import api.entities.Tile;

/***
 * This mission used for attacking the enemies hills
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillMission extends PathMission {

    /***
     * the enemy hill.
     */
    Tile enemyHill;

    public AttackHillMission(List<Tile> path) {
        super(path);
        enemyHill = path.get(path.size() - 1);
    }

    /***
     * mission is as long vaild, as long the enemy hill exists.
     */
    @Override
    protected boolean isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(enemyHill))
            return false;
        return true;
    }

}
