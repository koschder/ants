package starter.tasks;

import java.util.Map;

import starter.Ant;
import starter.Ants;
import starter.Tile;

public class CombatTask extends BaseTask {

	@Override
	public void perform(Ants ants, Map<Tile, Tile> orders) {

		for (Ant enemy : ants.getEnemyAnts()) {
			final Tile enemyLoc = enemy.getTile();
			for (Ant myAnt : ants.getMyAnts()) {
				final Tile myLoc = myAnt.getTile();
				final int distance = ants.getDistance(myLoc, enemyLoc);
				enemy.addEnemy(myLoc, distance);
				myAnt.addEnemy(enemyLoc, distance);
			}
		}
	}

}
