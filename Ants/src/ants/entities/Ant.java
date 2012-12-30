package ants.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ants.state.Ants;
import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;

public class Ant implements Comparable<Ant>, Unit {
    public static final int MINE = 0;
    private Tile tile;
    private Tile nextTile;
    private int player;
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
        return tile.toString() + ", Player=" + player;
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

    public Tile getPathEnd() {
        if (hasPath())
            return path.get(path.size() - 1);
        return null;
    }

    public String visualizeInfo() {
        if (hasPath())
            return tile + " path: " + getPath();
        return tile.toString();
    }

    public Aim currentDirection() {
        if (!hasPath())
            return null;
        Tile tile = getPath().get(0);
        return Ants.getWorld().getDirections(getTile(), tile).get(0);
    }
}
