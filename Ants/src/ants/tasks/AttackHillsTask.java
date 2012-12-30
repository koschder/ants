package ants.tasks;

import ants.missions.AttackHillMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Task that identifies enemy hills suitable for attacking and sends Ants to attack them.
 * 
 * @author kases1, kustl1
 * 
 */
public class AttackHillsTask extends BaseTask {

    @Override
    public void doPerform() {
        // set up one hill mission each enemy hill
        for (Tile hillLoc : Ants.getWorld().getEnemyHills()) {
            boolean created = false;
            for (Mission m : Ants.getOrders().getMissions()) {
                if (m instanceof AttackHillMission) {
                    AttackHillMission am = (AttackHillMission) m;
                    if (am.getHill().equals(hillLoc)) {
                        created = true;
                        break;
                    }
                }
            }
            if (!created) {
                addMission(new AttackHillMission(hillLoc));
            }
        }
    }

    @Override
    public Type getType() {
        return Type.ATTACK_HILLS;
    }

}
