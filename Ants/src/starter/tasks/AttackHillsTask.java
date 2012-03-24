package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import starter.Ant;
import starter.Ants;
import starter.Route;
import starter.Tile;

public class AttackHillsTask extends BaseTask {

	Set<Tile> enemyHills = new HashSet<Tile>();

	@Override
	public void prepare(Ants ants) {
		for (Iterator<Tile> iterator = enemyHills.iterator(); iterator
				.hasNext();) {
			if (!ants.isVisible(iterator.next()))
				iterator.remove();
		}
	}

	@Override
	public void perform(Ants ants, Map<Tile, Tile> orders) {
		// add new hills to set
		for (Tile enemyHill : ants.getEnemyHills()) {
			if (!enemyHills.contains(enemyHill)) {
				enemyHills.add(enemyHill);
			}
		}
		// attack hills
		List<Route> hillRoutes = new ArrayList<Route>();
		for (Tile hillLoc : enemyHills) {
			for (Ant ant : ants.getMyAnts()) {
				final Tile tile = ant.getTile();
				if (!orders.containsValue(tile)) {
					int distance = ants.getDistance(tile, hillLoc);
					Route route = new Route(tile, hillLoc, distance);
					hillRoutes.add(route);
				}
			}
		}
		Collections.sort(hillRoutes);
		for (Route route : hillRoutes) {
			doMoveLocation(ants, route.getStart(), route.getEnd(), orders);
		}
	}

}
