package ants.tasks;

import ants.missions.AttackHillMission;
import ants.missions.Mission;
import ants.state.Ants;
import api.entities.Tile;

/**
 * Task that identifies enemy hills suitable for attacking and sends Ants to attack them.
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillsTask extends BaseTask {

    // private int startFlockAttackTurn = 220;
    // private int startNonFlockAttackTurn = 5;

    @Override
    public void doPerform() {
        // if (Ants.getAnts().getTurn() >= startNonFlockAttackTurn)
        doPerformNonFlockAttack();
        // if (Ants.getAnts().getTurn() >= startFlockAttackTurn)
        // doPerformFlockAttack();
    }

    /*
     * private void doPerformFlockAttack() { for (Tile enemyHill : Ants.getWorld().getEnemyHills()) { for (Tile myHill :
     * Ants.getWorld().getMyHills()) { if (isMissionActive(enemyHill, myHill)) continue; List<Tile> path =
     * Ants.getPathFinder().search(Strategy.AStar, myHill, enemyHill, 40); if (path != null) { // concentrate Ants near
     * my Hill Tile ralleyPoint = path.size() <= 10 ? path.get(path.size() / 2) : path.get(10); addMission(new
     * AttackHillsInFlockMission(enemyHill, myHill, ralleyPoint, 3, 20)); } } } }
     * 
     * 
     * private boolean isMissionActive(Tile hill, Tile myHill) { for (Mission mission : Ants.getOrders().getMissions())
     * { if (mission instanceof AttackHillsInFlockMission) { if (((AttackHillsInFlockMission)
     * mission).getHill().equals(hill) || ((AttackHillsInFlockMission) mission).getStartPoint().equals(myHill)) return
     * true; } } return false; }
     */
    private void doPerformNonFlockAttack() {

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
