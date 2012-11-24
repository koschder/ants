package ants.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import api.entities.Tile;
import api.entities.Unit;

public class Ant implements Comparable<Ant>, Unit, Cloneable {
    public static final int MINE = 0;
    private Tile tile;
    private Tile nextTile;
    // private Mission mission;
    private int player;
    private Map<Ant, Integer> enemies = new HashMap<Ant, Integer>();
    private Map<Ant, Integer> friends = new HashMap<Ant, Integer>();
    private Map<Ant, Integer> sortedEnemies = null;
    private Map<Ant, Integer> sortedFriends = null;
    private List<Tile> path;
    private int turnsWaited;

    public Ant(Tile tile, int owner) {
        this.tile = tile;
        this.player = owner;
    }

    @Override
    public boolean isMine() {
        return player == MINE;
    }

    @Override
    public int getPlayer() {
        return player;
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

    /**
     * Returns all friendly ants in a radius of my ant.
     * 
     * @param radius
     * @return
     */
    public List<Ant> getFriendsInRadius(int radius) {
        final List<Ant> friends = new ArrayList<Ant>();
        for (Entry<Ant, Integer> entry : getSortedFriends().entrySet()) {
            if (entry.getValue() > radius)
                break;
            friends.add(entry.getKey());
        }
        return friends;
    }

    /**
     * returns all emenies in a radius
     * 
     * @param radius
     * @param onlyMyAnts
     *            to decide if alle emenies are returned, or only the current bot ones.
     * @return
     */
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

    @Override
    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    /***
     * places the ant on the tile, where it was moved in the last round.
     */
    public void setup() {
        if (nextTile != null) {
            tile = new Tile(nextTile.getRow(), nextTile.getCol());
            nextTile = null;
        }

    }

    public static class DistanceComparator implements Comparator<Ant> {

        Map<Ant, Integer> base;

        public DistanceComparator(Map<Ant, Integer> base) {
            this.base = base;
        }

        public int compare(Ant a, Ant b) {
            return base.get(a).compareTo(base.get(b));
        }
    }

    @Override
    public int compareTo(Ant o) {
        return tile.compareTo(o.getTile());
    }

    @Override
    public int hashCode() {
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

    @Override
    public String toString() {
        // String paths = path != null ? " , path= " + path : "";
        // return tile.toString() + paths;
        return tile.toString() + ", Player=" + player;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    public List<Tile> getPath() {
        return path;
    }

    public void setPath(List<Tile> p) {
        if (p != null && p.size() > 0 && p.get(0).equals(getTile())) {
            p.remove(0);
        }
        this.path = p;
    }

    public void resetTurnsWaited() {
        this.turnsWaited = 0;
    }

    public void incrementTurnsWaited() {
        this.turnsWaited++;
    }

    public int getTurnsWaited() {
        return this.turnsWaited;
    }

    public boolean hasPath() {
        return path != null && path.size() > 0;
    }
}
