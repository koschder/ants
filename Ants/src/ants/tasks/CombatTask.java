package ants.tasks;


/**
 * This task identifies vulnerable enemy ants and sends a troop of our ants to attack them.
 * 
 * @author kases1, kustl1
 * 
 */
public class CombatTask extends BaseTask {

    @Override
    public void doPerform() {
        // for (Ant enemy : Ants.getPopulation().getEnemyAnts()) {
        // List<Ant> myAntsInRange = enemy.getEnemiesInRadius(Ants.getWorld().getViewRadius2(), true);
        // removeEmployedAnts(myAntsInRange);
        // List<Ant> friends = enemy.getFriendsInRadius(Ants.getWorld().getViewRadius2());
        // if (myAntsInRange.size() > friends.size())
        // attackEnemy(enemy, friends);
        // }
    }

    // private void removeEmployedAnts(List<Ant> myAntsInRange) {
    // Collection<Ant> myUnemployed = Ants.getPopulation().getMyUnemployedAnts();
    // for (Iterator<Ant> iterator = myAntsInRange.iterator(); iterator.hasNext();) {
    // if (!myUnemployed.contains(iterator.next()))
    // iterator.remove();
    // }
    // }

    // private void attackEnemy(Ant enemy, List<Ant> friends) {
    // for (Ant ant : friends) {
    // List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, ant.getTile(), enemy.getTile());
    // if (path != null)
    // addMission(new AttackAntMission(ant, path));
    // }
    // }

    @Override
    public Type getType() {
        return Type.COMBAT;
    }
}
