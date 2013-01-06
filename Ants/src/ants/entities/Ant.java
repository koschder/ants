package ants.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import api.entities.Tile;
import api.entities.Unit;

/**
 * This class represents an ant on the map
 * 
 * @author kases1, kustl1
 * 
 */
public class Ant implements Comparable<Ant>, Unit {
    /**
     * This constant denotes the value for getPlayer() that means the ant is ours.
     */
    public static final int MINE = 0;
    private Tile tile;
    private Tile nextTile;
    private int player;
    private List<Tile> path;
    private int turnsWaited;

    /**
     * Default constructor for the Ant
     * 
     * @param tile
     * @param owner
     */
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

    @Override
    public Tile getTile() {
        return tile;
    }

    /**
     * Sets the tile the ant will be standing on next turn, barring unforeseen circumstances.
     * 
     * @param nextTile
     */
    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    /**
     * places the ant on the tile, where it was moved in the last round.
     */
    public void setup() {
        if (nextTile == null) {
            turnsWaited++;
        } else {
            tile = new Tile(nextTile.getRow(), nextTile.getCol());
            nextTile = null;
        }

    }

    /**
     * This Class compares two ants according to their distance from a given tile.
     * 
     * @author kases1, kustl1
     * 
     */
    public static class DistanceComparator implements Comparator<Ant> {

        Map<Ant, Integer> base;

        /**
         * Default Constructor for {@link DistanceComparator}
         * 
         * @param base
         *            a map of Ants and their distances from the reference Tile
         */
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
        return tile.toString() + ", Player=" + player;
    }

    /**
     * 
     * @return the path the ant is currently following
     */
    public List<Tile> getPath() {
        return path;
    }

    /**
     * Set a path for the ant to follow
     * 
     * @param p
     */
    public void setPath(List<Tile> p) {
        if (p != null && p.size() > 0 && p.get(0).equals(getTile())) {
            p.remove(0);
        }
        this.path = p;
    }

    /**
     * reset turnsWaited to 0
     */
    public void resetTurnsWaited() {
        this.turnsWaited = 0;
    }

    /**
     * increment turnsWaited
     */
    public void incrementTurnsWaited() {
        this.turnsWaited++;
    }

    /**
     * 
     * @return how many turns this ant has spent waiting
     */
    public int getTurnsWaited() {
        return this.turnsWaited;
    }

    /**
     * 
     * @return true if the ant has a path set
     */
    public boolean hasPath() {
        return path != null && path.size() > 0;
    }

    /**
     * 
     * @return the end of the path the ant is following, null if the ant has no path
     */
    public Tile getPathEnd() {
        if (hasPath())
            return path.get(path.size() - 1);
        return null;
    }

    /**
     * 
     * @return information for the visualizer
     */
    public String visualizeInfo() {
        if (hasPath())
            return tile + " path: " + getPath();
        return tile.toString();
    }

}
