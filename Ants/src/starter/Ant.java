package starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Ant implements Comparable<Ant> {
    static final int MINE = 0;
    private Tile tile;
    private Tile nextTile;
    private int player;
    private Map<Ant, Integer> enemies = new HashMap<Ant, Integer>();
    private Map<Ant, Integer> friends = new HashMap<Ant, Integer>();
    private Map<Ant, Integer> sortedEnemies = null;
    private Map<Ant, Integer> sortedFriends = null;

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

    public Tile getNextTile() {
        return nextTile;
    }

    public void addEnemy(Ant enemy, int distance) {
        enemies.put(enemy, distance);
    }

    public void addFriend(Ant friend, int distance) {
        friends.put(friend, distance);
    }

    private Map<Ant, Integer> getSortedEnemies() {
        if (sortedEnemies == null)
            sortedEnemies = new TreeMap<Ant, Integer>(new DistanceComparator(enemies));
        return sortedEnemies;
    }

    private Map<Ant, Integer> getSortedFriends() {
        if (sortedFriends == null)
            sortedFriends = new TreeMap<Ant, Integer>(new DistanceComparator(friends));
        return sortedFriends;
    }

    public List<Ant> getFriendsInRadius(int radius) {
        final List<Ant> friends = new ArrayList<Ant>();
        for (Entry<Ant, Integer> entry : getSortedFriends().entrySet()) {
            if (entry.getValue() > radius)
                break;
            friends.add(entry.getKey());
        }
        return friends;
    }

    public List<Ant> getEnemiesInRadius(int radius, boolean onlyMyAnts) {
        final List<Ant> enemies = new ArrayList<Ant>();
        for (Entry<Ant, Integer> entry : getSortedEnemies().entrySet()) {
            if (entry.getValue() > radius)
                break;
            if (!onlyMyAnts || entry.getKey().isMine())
                enemies.add(entry.getKey());
        }
        return enemies;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    public void setup() {
        if (nextTile != null) {
            tile = new Tile(nextTile.getRow(), nextTile.getCol());
            nextTile = null;
        }

    }

    @Override
    public int compareTo(Ant o) {
        return tile.compareTo(o.getTile());
    }

    @Override
    public int hashCode() {
        // TODO darf man das so machen
        return tile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ant other = (Ant) obj;

        return other.getTile().equals(getTile());
    }

}
