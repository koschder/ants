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

        Vertex v = getCluster().getVertex(getStart());
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if (v == null)
            return list;

        for (Edge e : v.edges) {
            DirectedEdge de = new DirectedEdge(v, e.v1 == v ? e.v1 : v2, getCluster());
            Logger.debug(LogCategory.CLUSTERED_ASTAR, "Find neighbour for edge %s in cluster %s ", de.toShortString(),
                    getCluster());
            list.addAll(getCluster().getEdgeWithNeighbourCluster(de));
        }

        return list;
    }

    @Override
    public boolean isSearchable(boolean bParentNode) {
        // TODO correct?
        return true;
    }

    @Override
    public int distanceTo(SearchTarget dest) {
        getEnd().manhattanDistanceTo(dest.getTargetTile());
        return 0;
    }

    @Override
    public List<Tile> getPath() {
        if (path != null)
            return path;

        return PathFinder.bestPath(PathFinder.SIMPLE, getStart(), getEnd());
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
        return super.equals(other) && getCluster().equals(other.getCluster());
    }

    @Override
    public String toShortString() {
        return super.toString();
    }

}
