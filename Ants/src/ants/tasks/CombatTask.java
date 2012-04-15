package ants.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ants.entities.Ant;
import ants.entities.Tile;
import ants.missions.AttackMission;
import ants.missions.Mission;
import ants.search.PathFinder;
import ants.state.Ants;

public class CombatTask extends BaseTask {

    @Override
    public void perform() {
        Collection<Ant> myUnemployed = Ants.getPopulation().getMyUnemployedAnts();
        for (Ant enemy : Ants.getPopulation().getEnemyAnts()) {
            // TODO getXXInRadius expects manhattan, not r^2
            List<Ant> myAntsInRange = enemy.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), true);
            removeEmployedAnts(myUnemployed, myAntsInRange);
            List<Ant> friends = enemy.getFriendsInRadius(Ants.getWorld().getViewRadius2());
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
            List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, ant.getTile(), enemy.getTile());
            if (path.size() <= 2) {
                Ants.getOrders().moveToNextTile(ant, path);
            } else {
                Mission mission = new AttackMission(ant, path);
                Ants.getOrders().addMission(mission);
                mission.execute();
            }
        }

    }
}
