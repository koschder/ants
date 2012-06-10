package ants.entities;

import java.util.ArrayList;
import java.util.List;

import ants.search.Cluster;
import ants.search.PathFinder;

public class DirectedEdge extends Edge implements SearchTarget {

    private Tile startTile;

    public DirectedEdge(Tile vertices, Tile lastVertices, Cluster c) {
        super(vertices, lastVertices, c, null);
        startTile = vertices;
    }

    public void reverseEdge() {
        if (startTile == getTile1())
            startTile = getTile2();
        else
            startTile =  getTile1();
    }

    @Override
    public List<SearchTarget> getSuccessors() {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        list.addAll(getCluster().getEdgeWithNeighbourCluster(this));
        return list;
    }

    @Override
    public boolean isSearchable(boolean bParentNode) {
        // TODO correct?
        return true;
    }

    @Override
    public int distanceTo(SearchTarget dest) {
        return getEnd().manhattanDistanceTo(dest.getTargetTile());
    }

    @Override
    public List<Tile> getPath() {
        if (path != null)
            return path;

        List<Tile> tiles = PathFinder.bestPath(PathFinder.SIMPLE, getEnd(), getStart());
        if (tiles.size() > 1)
            tiles.remove(tiles.size() - 1);

        return tiles;
    }

    public Tile getEnd() {
        return  getTile1().equals(startTile) ?  getTile2() :  getTile1();
    }

    public Tile getStart() {
        return this.startTile;
    }

    @Override
    public boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2) {
        return true;
    }

    @Override
    public Tile getTargetTile() {
        return getStart();
    }

    @Override
    public String toString() {
        return super.toString() + " " + getCluster();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DirectedEdge other = (DirectedEdge) obj;
        if (getCluster() == null && other.getCluster() == null)
            return super.equals(other);
        else if (getCluster() != null && other.getCluster() != null)
            return super.equals(other) && getCluster().equals(other.getCluster());

        return false;
    }

    @Override
    public String toShortString() {
        return getStart() + "-" + getEnd();
    }

    @Override
    public boolean isFinal(SearchTarget to) {
        return getEnd().equals(to.getTargetTile()) || getStart().equals(to.getTargetTile());
       // return 

    }

    @Override
    public int getCost() {
        if (getPath() != null)
            return getPath().size();

        return Math.abs(getStart().getRow() - getEnd().getRow()) + Math.abs(getStart().getCol() - getEnd().getCol());

    }

}
