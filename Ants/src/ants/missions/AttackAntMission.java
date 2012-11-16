package ants.missions;

import java.util.*;

import ants.entities.*;
import api.entities.*;

/***
 * This mission is created for attacking enemy ants.
 * 
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
    protected String isSpecificMissionValid() {
        // TODO check if enemies is still there
        return null;
    }

}
