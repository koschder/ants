package starter.tasks;

import java.util.Map;

import starter.Ants;
import starter.Tile;

public interface Task {
	public void perform(Ants ants, Map<Tile, Tile> orders);
	public void prepare(Ants ants);
}
