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

/***
 * a cluster is an area on the map. the cluster connects the neighbour cluster throw passable edges along the cluster
 * side. these edges are connected to edge other is there is a passable path between.
 * 
 * @author kaeserst
 * 
 */
public class Cluster {

    /***
     * stores all edges located in the cluster
     */
    public List<Edge> edges = new ArrayList<Edge>();
    /***
     * stores all vertices aka nodes located in the cluster.
     */
    public List<Vertex> vertices = new ArrayList<Vertex>();
    /***
     * name of the cluster conists of the index and the dimensions.
     */
    public String name;
    /***
     * the index of the cluster.
     */
    public int index;
    /***
     * row of the cluster referring to the whole clustering
     */
    private int row;
    /***
     * col of the cluster reffering to the whole clustering.
     */
    private int col;
    /***
     * the height and weight of the cluster.
     */
    private int clusterSize;
    /***
     * clustering of who this cluster is part of.
     */
    private Clustering clustering;

    /***
     * the cluster sides which are already scanned.
     */
    public Set<Aim> scannedAims = new HashSet<Aim>();

    /***
     * initialize a cluster
     * 
     * @param r
     * @param c
     * @param csize
     * @param idx
     * @param clstering
     */
    public Cluster(int r, int c, int csize, int idx, Clustering clstering) {
        index = idx;
        clusterSize = csize;
        row = r;
        col = c;
        clustering = clstering;
        name = "Cluster Idx:" + index + " Dimension R:" + r * clusterSize + " C:" + c * clusterSize + "xR:" + (r + 1)
                * clusterSize + " C:" + (c + 1) * clusterSize;
    }

    public Set<Aim> getAims() {
        return scannedAims;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public List<Edge> getEdges() {

        return this.edges;
    }

    /***
     * if a new edge is added to the cluster we connect it with all existing edges.
     * 
     * @param newEdge
     */
    private void createNewPath(Edge newEdge) {
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            findNewEdge(e.getTile1(), newEdge.getTile1());
            findNewEdge(e.getTile2(), newEdge.getTile2());
            findNewEdge(e.getTile1(), newEdge.getTile2());
            findNewEdge(e.getTile2(), newEdge.getTile1());
        }

    }

    /***
     * for all new edges added we connect them with the existing edges.
     * 
     * @param newEdges
     */
    private void createNewPath(List<Edge> newEdges) {
        for (Edge e : newEdges) {
            Logger.debug(LogCategory.CLUSTERING_Detail, "%s: createNewPath for e: %s", name, e);
            createNewPath(e);
        }
    }

    /***
     * print all edges of the cluster into the log file.
     */
    public void debugEdges() {
        Logger.debug(LogCategory.PATHFINDING, "Edges of %s", name);
        Logger.debug(LogCategory.PATHFINDING, "Done_aims_of_%s", scannedAims.toString());
        for (Edge e : edges) {
            Logger.debug(LogCategory.PATHFINDING, "xx: Edge from %s to %s type: %s", e.getTile1(), e.getTile2(),
                    e.getType());
        }
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

    /***
     * connecting two tiles (nodes) with each other by creation a new edge.
     * 
     * @param tStart
     * @param tEnd
     */
    private void findNewEdge(Tile tStart, Tile tEnd) {
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: Find new path between %s and %s", name, tStart, tEnd);
        if (tStart.equals(tEnd))
            return;

        Edge edge = new Edge(tStart, tEnd, this, Edge.EdgeType.Intra);
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

    /***
     * returns all edges laying on a specific side defined by the aim
     * 
     * @param a
     * @return an edge or null if no edge is found.
     */
    public Edge getEdgeOnBoarder(Aim a) {
        for (Edge e : edges) {
            if (e.getType().equals(getEdgeType(a))) {
                return e;
            }
        }
        return null;
    }

    /***
     * 
     * @param a
     * @return edge type for an aim
     */
    private EdgeType getEdgeType(Aim a) {
        if (a.equals(Aim.SOUTH))
            return EdgeType.South;
        else if (a.equals(Aim.NORTH))
            return EdgeType.North;
        else if (a.equals(Aim.EAST))
            return EdgeType.East;
        else if (a.equals(Aim.WEST))
            return EdgeType.West;

        return null;
    }

    /***
     * returns all successor node of a DirectedEdge in the and in the neighbor clusters.
     * 
     * @param e
     * @return
     */
    public List<SearchTarget> getEdgeWithNeighbourCluster(DirectedEdge e) {
        List<SearchTarget> list = new ArrayList<SearchTarget>();

        list.addAll(getEdgesOfCluster(e.getEnd(), row, col));

        if (e.getEnd().getRow() == getBottomFrontier()) // south todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row + 1, col));

        if (e.getEnd().getRow() == getTopFrontier()) // north todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row - 1, col));

        if (e.getEnd().getCol() == getLeftFrontier()) // west todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row, col - 1));

        if (e.getEnd().getCol() == getRightFrontier()) // east todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row, col + 1));

        Logger.debug(LogCategory.CLUSTERED_ASTAR, "%s neighbour cluster found", list.size());
        for (SearchTarget t : list)
            Logger.debug(LogCategory.CLUSTERED_ASTAR, "      => %s", t);
        return list;
    }

    /***
     * 
     * @return the col position of left side of the cluster
     */
    private int getLeftFrontier() {
        return col * clusterSize;
    }

    /***
     * 
     * @return the col position of right side of the cluster
     */
    private int getRightFrontier() {

        return (col + 1) * clusterSize;
    }

    /***
     * 
     * @return the row position of top side of the cluster
     */
    private int getTopFrontier() {
        return row * clusterSize;
    }

    /***
     * 
     * @return the row position of bottom side of the cluster
     */
    private int getBottomFrontier() {
        return (row + 1) * clusterSize;
    }

    /***
     * 
     * @param tile
     * @return a an existing vertex on the tile
     */
    public Vertex getVertex(Tile tile) {
        if (vertices.contains(tile))
            return vertices.get(vertices.indexOf(tile));
        return null;
    }

    /***
     * returns all continuative edges of the cluster[clusterRow][clusterCol]
     * 
     * @param start
     * @param clusterRow
     * @param clusterCol
     * @return
     */
    private List<SearchTarget> getEdgesOfCluster(Tile start, int clusterRow, int clusterCol) {
        Cluster c = clustering.getClusterWrapAround(clusterRow, clusterCol);
        Logger.debug(LogCategory.PATHFINDING, "search edge starts with %s in %s", start, c);
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        for (Edge e : c.edges) {
            // Logger.debug(LogCategory.PATHFINDING, "scan edge %s ", e);
            if (e.getTile1().equals(start)) {
                list.add(new DirectedEdge(e.getTile1(), e.getTile2(), c));
            } else if (e.getTile2().equals(start)) {
                list.add(new DirectedEdge(e.getTile2(), e.getTile1(), c));
            }
        }
        Logger.debug(LogCategory.PATHFINDING, "edge found %s they are: %s", list.size(), list);
        return list;
    }

    /***
     * 
     * @param checkAim
     * @return true if this aim is already processed.
     */
    public boolean hasScan(Aim checkAim) {
        return scannedAims.contains(checkAim);
    }

    /***
     * 
     * @return true if all four side of the cluster ar processed.
     */
    public boolean isClustered() {
        return scannedAims.size() == 4;
    }

    /***
     * the vertices of the new edges are connected with the already existing vertices.
     * 
     * @param newEdges
     * @param newaim
     */
    private void processNewEdgesVertices(List<Edge> newEdges, Aim newaim) {
        for (Edge e : newEdges) {
            e.setEdgeType(getEdgeType(newaim));
            e.setCluster(this);
            processVertex(e, e.getTile1());
            processVertex(e, e.getTile2());
        }

    }

    /***
     * add the vertices to the vertices list if they aren't yet.
     * 
     * @param e
     * @param t
     */
    private void processVertex(Edge e, Tile t) {
        if (vertices.contains(t))
            vertices.get(vertices.indexOf(t)).addEdge(e);
        else
            vertices.add(new Vertex(e.getTile1(), e));
    }

    /***
     * new scanned edges are integrated into the cluster and are connected with existing edges.
     * @param newaim
     * @param newEdges
     */
    public void SetCluster(Aim newaim, List<Edge> newEdges) {
        scannedAims.add(newaim);
        Logger.debug(LogCategory.CLUSTERING_Detail, "%s: New edges arrived from aim %s E: %s", name, newaim, newEdges);
        if (newEdges.size() > 0) {

            List<Edge> hardCopy = new ArrayList<Edge>();
            for (Edge e : newEdges) {
                hardCopy.add(new Edge(e.getTile1(), e.getTile2(), e.getPath(), e.getCluster()));
            }
            edges.addAll(hardCopy);
            processNewEdgesVertices(hardCopy, newaim);
            createNewPath(hardCopy);
            debugEdges();
            if (isClustered()) {
                Logger.debug(LogCategory.CLUSTERING, "%s is clustered_now! Vertices: %s Edges: %s", name,
                        vertices.size(), edges.size());
            }
        }

    }

    @Override
    public String toString() {
        return name + " Scanned aims: " + scannedAims + " Edge: " + edges.size() + " Vertices: " + vertices.size();
    }

}
