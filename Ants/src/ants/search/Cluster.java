package ants.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ants.entities.Aim;
import ants.entities.Tile;
import ants.entities.Vertex;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Cluster {

    public List<Edge> edges = new ArrayList<Edge>();
    public List<Vertex> vertices = new ArrayList<Vertex>();
    public String name;
    public int index;
    public Set<Aim> aims = new HashSet<Aim>();

    public Cluster(int r, int c, int clusterSize) {
        index = r * clusterSize + c;
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

        Edge edge = new Edge(tStart, tEnd);
        if (edges.contains(edge))
            return;

        // path limit costs are the manhattanDistance plus a factor for a maybe way around
        int costs = tStart.manhattanDistanceTo(tEnd) + 3;
        List<Tile> path = PathFinder.bestPath(PathFinder.SIMPLE, tStart, tEnd, costs);
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

}
