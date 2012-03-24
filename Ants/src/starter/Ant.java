package starter;

import java.util.HashMap;
import java.util.Map;

public class Ant implements Comparable<Ant> {
	static final int MINE = 0;
	private Tile tile;
	private int player;
	private Map<Tile, Integer> enemies = new HashMap<Tile, Integer>();
	private Map<Tile, Integer> friends = new HashMap<Tile, Integer>();

	public Ant(Tile tile, int owner) {
		this.tile = tile;
		this.player = owner;
	}

	public boolean isMine() {
		return player == MINE;
	}

	public int getPlayer() {
		return player;
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

	@Override
	public boolean equals(Object obj) {
		return tile.equals(obj);
	}

	public void addFriend(Tile friendLoc, int distance) {
		friends.put(friendLoc, distance);
	}
}
