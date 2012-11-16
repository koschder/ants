package ants.missions;

import java.util.*;

import ants.entities.*;
import ants.state.*;
import api.entities.*;

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

    public AttackHillMission(Ant ant, List<Tile> path) {
        super(ant, path);
        enemyHill = path.get(path.size() - 1);
    }

    /***
     * mission is as long vaild, as long the enemy hill exists.
     */
    @Override
    protected String isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(enemyHill))
            return "Enemy hill " + enemyHill + " is no longer there";
        return null;
    }

}
