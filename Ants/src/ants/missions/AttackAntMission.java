package ants.missions;

import java.util.List;



import ants.entities.Ant;
import api.Tile;

/***
 * This mission is created for attacking enemy ants.
 * @author kases1,kustl1
 *
 */
public class AttackAntMission extends PathMission {

    public AttackAntMission(Ant ant, List<Tile> path) {
        super(ant, path);
    }

    /***
     * is always valid
     */
    @Override
    protected boolean isSpecificMissionValid() {
        // TODO check if enemies is still there
        return true;
    }

}
