package starter.tasks;

import java.util.List;
import java.util.Map;

import starter.Aim;
import starter.Ants;
import starter.Logger;
import starter.Tile;

public abstract class BaseTask implements Task {

	public BaseTask() {
		super();
	}

	protected boolean doMoveDirection(Ants ants, Tile antLoc, Aim direction,
			Map<Tile, Tile> orders) {
		// Track all moves, prevent collisions
		Tile newLoc = ants.getTile(antLoc, direction);
		if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
			ants.issueOrder(antLoc, direction);
			orders.put(newLoc, antLoc);
			Logger.log("%1$s: Moving ant from %2$s to %3$s", getClass()
					.getSimpleName(), antLoc, newLoc);
			return true;
		} else {
			return false;
		}
	}

	protected boolean doMoveLocation(Ants ants, Tile antLoc, Tile destLoc,
			Map<Tile, Tile> orders) {
		// Track targets to prevent 2 ants to the same location
		List<Aim> directions = ants.getDirections(antLoc, destLoc);
		for (Aim direction : directions) {
			if (doMoveDirection(ants, antLoc, direction, orders)) {
				return true;
			}
		}
		return false;
	}

	public void prepare(Ants ants) {

	}

}