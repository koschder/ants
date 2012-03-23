package starter;

import java.util.HashMap;
import java.util.Map;

public class Ant implements Comparable<Ant> {
	private Tile tile;
	private Map<Tile, Integer> enemies = new HashMap<Tile, Integer>();

	public Ant(Tile tile) {
		this.tile = tile;
	}

	public void addEnemy(Tile enemy, int distance) {
		enemies.put(enemy, distance);
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	@Override
	public int compareTo(Ant o) {
		return tile.compareTo(o.getTile());
	}
}
