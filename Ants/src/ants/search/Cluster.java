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
import ants.entities.Edge.EdgeType;
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
    private Clustering clustering;
    public Set<Aim> aims = new HashSet<Aim>();


    public Set<Aim> getAims() {
        return aims;
    }

    public Cluster(int r, int c, int csize,int idx, Clustering clstering) {
        index = idx;
        clusterSize = csize;
        row = r;
        col = c;
        clustering = clstering;
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
        Logger.debug(LogCategory.PATHFINDING, "Edges of %s", name);
        Logger.debug(LogCategory.PATHFINDING, "Done_aims_of_%s", aims.toString());
        for (Edge e : edges) {
            Logger.debug(LogCategory.PATHFINDING, "xx: Edge from %s to %s type: %s", e.getTile1(), e.getTile2(), e.getType());
        }
    }

    public void SetCluster(Aim newaim, List<Edge> newEdges) {
        aims.add(newaim);
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: New edges arrived from aim %s E: %s", name, newaim, newEdges);
        if(newEdges.size() > 0){
        
        List<Edge> hardCopy = new ArrayList<Edge>(); 
        for(Edge e : newEdges){   
            hardCopy.add(new Edge(e.getTile1(),e.getTile2(),e.getPath(),e.getCluster()));
        }
        edges.addAll(hardCopy);
        
        processNewEdgesVertices(hardCopy,newaim);
        createNewPath(hardCopy);
        debugEdges();
        if (isClustered()) {
            Logger.debug(LogCategory.CLUSTERING, "%s is clustered_now! Vertices: %s Edges: %s", name, vertices.size(),
                    edges.size());
        }
        }

    }

    private void processNewEdgesVertices(List<Edge> newEdges,Aim newaim) {
        for (Edge e : newEdges) {
            e.setEdgeType(getEdgeType(newaim));
            e.setCluster(this);
            processVertex(e, e.getTile1());
            processVertex(e, e.getTile2());
        }

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    private void processVertex(Edge e, Tile t) {
        if (vertices.contains(t))
            vertices.get(vertices.indexOf(t)).addEdge(e);
        else
            vertices.add(new Vertex(e.getTile1(), e));
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
            FindNewEdge(e.getTile1(), newEdge.getTile1());
            FindNewEdge(e.getTile2(), newEdge.getTile2());
            FindNewEdge(e.getTile1(), newEdge.getTile2());
            FindNewEdge(e.getTile2(), newEdge.getTile1());
        }

    }

    private void FindNewEdge(Tile tStart, Tile tEnd) {
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: Find new path between %s and %s", name, tStart, tEnd);
        if (tStart.equals(tEnd))
            return;

        Edge edge = new Edge(tStart, tEnd, this,Edge.EdgeType.Intra);
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
        processVertex(edge, edge.getTile1());
        processVertex(edge, edge.getTile2());

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

        if (e.getEnd().getRow() == getBottomFrontier()) // south todo wrap around

            list.addAll(getWithWrapAround(e.getEnd(), row + 1, col));

        if (e.getEnd().getRow() == getTopFrontier()) // north todo wrap around

            list.addAll(getWithWrapAround(e.getEnd(), row - 1, col));

        if (e.getEnd().getCol() == getLeftFrontier()) // west todo wrap around

            list.addAll(getWithWrapAround(e.getEnd(), row, col - 1));

        if (e.getEnd().getCol() == getRightFrontier()) // east todo wrap around

            list.addAll(getWithWrapAround(e.getEnd(), row, col + 1));

        Logger.debug(LogCategory.CLUSTERED_ASTAR, "%s neighbour cluster found", list.size());
        for (SearchTarget t : list)
            Logger.debug(LogCategory.CLUSTERED_ASTAR, "      => %s", t);
        return list;
    }

    private List<SearchTarget> getWithWrapAround(Tile start, int row, int col2) {
        Cluster c = clustering.getWithWrapAround(row, col2);
        Logger.debug(LogCategory.PATHFINDING, "search edge starts with %s in %s", start, c);
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        for (Edge e : c.edges) {
            //Logger.debug(LogCategory.PATHFINDING, "scan edge %s ", e);
            if (e.getTile1().equals(start)) {
                list.add(new DirectedEdge(e.getTile1(), e.getTile2(), c));
            } else if (e.getTile2().equals(start)) {
                list.add(new DirectedEdge(e.getTile2(), e.getTile1(), c));
            }
        }
        Logger.debug(LogCategory.PATHFINDING, "edge found %s they are: %s", list.size(), list);
        // c.debugEdges();
        // Logger.debug(LogCategory.PATHFINDING,);
        return list;
    }

    private int getRightFrontier() {

        return (col + 1) * clusterSize;
    }

    private int getLeftFrontier() {
        return col * clusterSize;
    }

    private int getBottomFrontier() {
        return (row + 1) * clusterSize;
    }

    private int getTopFrontier() {
        return row * clusterSize;
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

    public Edge getEdgeOnBoarder(Aim a) {
       for(Edge e : edges){
           if(e.getType().equals(getEdgeType(a))){
               return e;
           }          
       }
       return null;
    }

    private EdgeType getEdgeType(Aim a) {
        if(a.equals(Aim.SOUTH))
            return EdgeType.South;
        else if(a.equals(Aim.NORTH))
            return EdgeType.North;
        else if(a.equals(Aim.EAST))
            return EdgeType.East;
        else if(a.equals(Aim.WEST))
            return EdgeType.West;
        
        return null;
    }

    public List<Edge> getEdges() {
        
        return this.edges;
    }

}
