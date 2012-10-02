package ants.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pathfinder.entities.Tile;

import ants.entities.Ant;
import ants.missions.AttackAntMission;
import ants.search.AntsPathFinder;
import ants.state.Ants;

/**
 * This task identifies vulnerable enemy ants and sends a troop of our ants to attack them.
 * 
 * @author kases1,kustl1
 * 
 */
public class CombatTask extends BaseTask {

    @Override
    public void perform() {
        for (Ant enemy : Ants.getPopulation().getEnemyAnts()) {
            List<Ant> myAntsInRange = enemy.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), true);
            removeEmployedAnts(myAntsInRange);
            List<Ant> friends = enemy.getFriendsInRadius(Ants.getWorld().getViewRadius2());
            if (myAntsInRange.size() > friends.size())
                attackEnemy(enemy, friends);
        }
    }

    private void removeEmployedAnts(List<Ant> myAntsInRange) {
        Collection<Ant> myUnemployed = Ants.getPopulation().getMyUnemployedAnts();
        for (Iterator<Ant> iterator = myAntsInRange.iterator(); iterator.hasNext();) {
            if (!myUnemployed.contains(iterator.next()))
                iterator.remove();
        }
    }

    private void attackEnemy(Ant enemy, List<Ant> friends) {
        for (Ant ant : friends) {
            List<Tile> path = Ants.getPathFinder().bestPath(AntsPathFinder.A_STAR, ant.getTile(), enemy.getTile());
            if (path != null)
                Ants.getOrders().addMission(new AttackAntMission(ant, path));
        }
    }
}
