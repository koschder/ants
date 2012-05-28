package ants.entities;

import java.util.ArrayList;
import java.util.List;

import ants.search.Cluster;
import ants.search.PathFinder;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class DirectedEdge extends Edge implements SearchTarget {

    private Tile startTile;

    public DirectedEdge(Tile vertices, Tile lastVertices, Cluster c) {
        super(vertices, lastVertices, c);
        startTile = vertices;
    }

    public void reverseEdge() {
        if (startTile == v1)
            startTile = v2;
        else
            startTile = v1;
    }

    @Override
    public List<SearchTarget> getSuccessors() {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        /*
         * Vertex v = getCluster().getVertex(getEnd()); if (v == null) return list;
         * 
         * Logger.debug(LogCategory.CLUSTERED_ASTAR, "Edges of %s",v); for (Edge e : v.edges) {
         * Logger.debug(LogCategory.CLUSTERED_ASTAR, "        =>%s",e);
         * 
         * }
         * 
         * for (Edge e : v.edges) { DirectedEdge de = new DirectedEdge(v, e.v1 == v ? e.v1 : e.v2, getCluster());
         * Logger.debug(LogCategory.CLUSTERED_ASTAR, "Find neighbour for edge %s in cluster %s ", de.toShortString(),
         * getCluster());
         */
        list.addAll(getCluster().getEdgeWithNeighbourCluster(this));
        // }

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

        List<Tile> tiles =  PathFinder.bestPath(PathFinder.SIMPLE, getEnd(),getStart());
        if(tiles.size()>1)
            tiles.remove(tiles.size()-1);
        
        return tiles;
    }

    public Tile getEnd() {
        return v1.equals(startTile) ? v2 : v1;
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
        return getEnd();
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
        return getEnd().equals(to.getTargetTile());

    }

}
