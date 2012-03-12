package starter.tasks;

import java.util.Map;

import starter.Aim;
import starter.Ants;
import starter.Tile;

public class ClearHillTask extends BaseTask {

	@Override
	public void perform(Ants ants, Map<Tile, Tile> orders) {
		// unblock hills
		for (Tile myHill : ants.getMyHills()) {
			if (ants.getMyAnts().contains(myHill)
					&& !orders.containsValue(myHill)) {
				for (Aim direction : Aim.values()) {
					if (doMoveDirection(ants, myHill, direction, orders)) {
						break;
					}
				}
			}
		}

	}

}
