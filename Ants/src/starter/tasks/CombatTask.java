package starter.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import starter.Ant;
import starter.Tile;
import starter.mission.AttackMission;
import starter.mission.Mission;

public class CombatTask extends BaseTask {

    @Override
    public void perform() {
        Collection<Ant> myUnemployed = ants.getMyUnemployedAnts();
        for (Ant enemy : ants.getEnemyAnts()) {
            // TODO getXXInRadius expects manhattan, not r^2
            List<Ant> myAntsInRange = enemy.getEnemiesInRadius(ants.getViewRadius2(), true);
            removeEmployedAnts(myUnemployed, myAntsInRange);
            List<Ant> friends = enemy.getFriendsInRadius(ants.getViewRadius2());
            if (myAntsInRange.size() > friends.size())
                attackEnemy(enemy, friends);
        }
    }

    private void removeEmployedAnts(Collection<Ant> myUnemployed, List<Ant> myAntsInRange) {
        for (Iterator<Ant> iterator = myAntsInRange.iterator(); iterator.hasNext();) {
            if (!myUnemployed.contains(iterator.next()))
                iterator.remove();
        }
    }

    private void attackEnemy(Ant enemy, List<Ant> friends) {
        for (Ant ant : friends) {
            List<Tile> path = search.bestPath(ant.getTile(), enemy.getTile());
            if (path.size() <= 2) {
                doMoveLocation(ant, path.get(0));
            } else {
                Mission mission = new AttackMission(ant, ants, path);
                ants.addMission(mission);
                mission.perform();
            }
        }

    }
}
