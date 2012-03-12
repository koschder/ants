package starter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import starter.Ants;
import starter.Route;
import starter.Tile;

public class GatherFoodTask extends BaseTask implements Task {

	
	@Override
	public void perform(Ants ants, Map<Tile, Tile> orders) {
		Map<Tile, Tile> foodTargets;
		List<Route> foodRoutes;
		TreeSet<Tile> sortedFood;
		TreeSet<Tile> sortedAnts;
		foodTargets = new HashMap<Tile, Tile>();
		// find close food
		foodRoutes = new ArrayList<Route>();
		sortedFood = new TreeSet<Tile>(ants.getFoodTiles());
		sortedAnts = new TreeSet<Tile>(ants.getMyAnts());

		for (Tile foodLoc : sortedFood) {
			for (Tile antLoc : sortedAnts) {
				int distance = ants.getDistance(antLoc, foodLoc);
				Route route = new Route(antLoc, foodLoc, distance);
				foodRoutes.add(route);
			}
		}
		Collections.sort(foodRoutes);
		for (Route route : foodRoutes) {
			if (!foodTargets.containsKey(route.getEnd())
					&& !foodTargets.containsValue(route.getStart())
					&& doMoveLocation(ants, route.getStart(), route.getEnd(),
							orders)) {
				foodTargets.put(route.getEnd(), route.getStart());
			}
		}

	}
}
