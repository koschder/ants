package ants.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ants.entities.Aim;
import ants.entities.DirectedEdge;
import ants.entities.Edge;
import ants.entities.SearchTarget;
import ants.entities.Tile;
import ants.entities.Vertex;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Cluster {

    public List<Edge> edges = new ArrayList<Edge>();
    public List<Vertex> vertices = new ArrayList<Vertex>();
    public String name;
    public int index;
    private int row;
    private int col;
    private int clusterSize;
    public Set<Aim> aims = new HashSet<Aim>();

    public Cluster(int r, int c, int csize) {
        index = r * csize + c;
        clusterSize = csize;
        row = r;
        col = c;
        name = "Cluster Idx:" + index + " Dimension R:" + r * clusterSize + " C:" + c * clusterSize + "xR:" + (r + 1)
                * clusterSize + " C:" + (c + 1) * clusterSize;
    }

    public boolean isClustered() {
        return aims.size() == 4;
    }

    public boolean isFullVisible() {
        return true;
    }

    public void debugEdges() {
        Logger.debug(LogCategory.CLUSTERING_Detail, "Edges of %s", name);
        Logger.debug(LogCategory.CLUSTERING_Detail, "Done_aims_of_%s", aims.toString());
        for (Edge e : edges) {
            Logger.debug(LogCategory.CLUSTERING_Detail, "xx: Edge from %s to %s", e.v1, e.v2);
        }
    }

    public void SetCluster(Aim newaim, List<Edge> newEdges) {
        aims.add(newaim);
        edges.addAll(newEdges);
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: New edges arrived from aim %s E: %s", name, newaim, newEdges);
        processNewEdgesVertices(newEdges);
        createNewPath(newEdges);
        debugEdges();
        if (isClustered()) {
            Logger.debug(LogCategory.CLUSTERING, "%s is clustered_now! Vertices: %s Edges: %s", name, vertices.size(),
                    edges.size());
        }

    }

    private void processNewEdgesVertices(List<Edge> newEdges) {
        for (Edge e : newEdges) {
            e.setCluster(this);
            processVertex(e, e.v1);
            processVertex(e, e.v2);
        }

    }

    private void processVertex(Edge e, Tile t) {
        if (vertices.contains(t))
            vertices.get(vertices.indexOf(t)).addEdge(e);
        else
            vertices.add(new Vertex(e.v1, e));
    }

    private void createNewPath(List<Edge> newEdges) {
        for (Edge e : newEdges) {
            Logger.debug(LogCategory.CLUSTERING_Detail, "%s: createNewPath for e: %s", name, e);
            createNewPath(e);
        }

    }

    private void createNewPath(Edge newEdge) {
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            FindNewEdge(e.v1, newEdge.v1);
            FindNewEdge(e.v2, newEdge.v2);
            FindNewEdge(e.v1, newEdge.v2);
            FindNewEdge(e.v2, newEdge.v1);
        }

    }

    private void FindNewEdge(Tile tStart, Tile tEnd) {
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: Find new path between %s and %s", name, tStart, tEnd);
        if (tStart.equals(tEnd))
            return;

        Edge edge = new Edge(tStart, tEnd, this);
        if (edges.contains(edge))
            return;

        // path limit costs are the manhattanDistance plus a factor for a maybe way around
        int costs = tStart.manhattanDistanceTo(tEnd) + 3;
        // List<Tile> path = PathFinder.bestPath(PathFinder.SIMPLE, tStart, tEnd, costs);
        Tile searchSpace0 = new Tile(row * clusterSize, col * clusterSize);
        // todo is +1 correct?
        Tile searchSpace1 = new Tile((row + 1) * clusterSize + 1, (col + 1) * clusterSize + 1);
        List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, tStart, tEnd, searchSpace0, searchSpace1, costs);
        // List<Tile> path = PathFinder.bestPath(PathFinder.SIMPLE, tStart, tEnd);
        if (path == null)
            return;

        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: found!!", name);
        edge.setPath(path);
        edges.add(edge);
        processVertex(edge, edge.v1);
        processVertex(edge, edge.v2);

    }

    public boolean hasScan(Aim checkAim) {
        return aims.contains(checkAim);
    }

    @Override
    public String toString() {
        return name + " Scanned aims: " + aims + " Edge: " + edges.size() + " Vertices: " + vertices.size();
    }

    public List<SearchTarget> getEdgeWithNeighbourCluster(DirectedEdge e) {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        // Logger.debug(LogCategory.CLUSTERED_ASTAR, "Looki looki at %s", e);
        if (e.getEnd().getRow() >= e.getStart().getRow()) // south todo wrap around
            list.add(new DirectedEdge(e.getStart(), e.getEnd(), Ants.getClusters().getWithWrapAround(row + 1, col)));
        if (e.getEnd().getRow() <= e.getStart().getRow()) // north todo wrap around
            list.add(new DirectedEdge(e.getStart(), e.getEnd(), Ants.getClusters().getWithWrapAround(row - 1, col)));

        if (e.getEnd().getCol() >= e.getStart().getCol()) // west todo wrap around
            list.add(new DirectedEdge(e.getStart(), e.getEnd(), Ants.getClusters().getWithWrapAround(row, col + 1)));
        if (e.getEnd().getCol() <= e.getStart().getCol()) // east todo wrap around
            list.add(new DirectedEdge(e.getStart(), e.getEnd(), Ants.getClusters().getWithWrapAround(row, col - 1)));

        Logger.debug(LogCategory.CLUSTERED_ASTAR, "%s neighbour cluster found. %s", list.size(), list);
        return list;
    }

    public Vertex getVertex(Tile start) {

        if (vertices.contains(start))
            return vertices.get(vertices.indexOf(start));

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cluster other = (Cluster) obj;
        return other.name == name;
    }

}
