package starter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Ant implements Comparable<Ant> {
	static final int MINE = 0;
	private Tile tile;
	private int player;
	private Map<Tile, Integer> enemies = new HashMap<Tile, Integer>();
	private Map<Tile, Integer> friends = new HashMap<Tile, Integer>();
	private Map<Tile, Integer> sortedEnemies = null;
	private Map<Tile, Integer> sortedFriends = null;

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

	public Map<Tile, Integer> getSortedEnemies() {
		if (sortedEnemies == null)
			sortedEnemies = new TreeMap<Tile, Integer>(new DistanceComparator(enemies));
		return sortedEnemies;
	}

	public Map<Tile, Integer> getSortedFriends() {
		if (sortedFriends == null)
			sortedFriends = new TreeMap<Tile, Integer>(new DistanceComparator(friends));
		return sortedFriends;
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

	class DistanceComparator implements Comparator<Tile> {

		Map<Tile, Integer> base;

		public DistanceComparator(Map<Tile, Integer> base) {
			this.base = base;
		}

		public int compare(Tile a, Tile b) {
			return base.get(a).compareTo(base.get(b));
		}
	}

}
