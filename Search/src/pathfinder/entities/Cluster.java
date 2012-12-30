package pathfinder.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.entities.Edge.EdgeType;
import api.entities.Aim;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

/**
 * a cluster is an area on the map. the cluster connects the neighbor cluster throw passable edges along the cluster
 * side. these edges are connected to edges inside the cluster if there is a passable path between.
 * 
 * @author kases1, kustl1
 * 
 */
public class Cluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CLUSTERING);
    private static final Logger LOGGER_PATH = LoggerFactory.getLogger(LogCategory.PATHFINDING);
    private static final Logger LOGGER_ASTAR = LoggerFactory.getLogger(LogCategory.CLUSTERED_ASTAR);

    /**
     * stores all edges located in the cluster
     */
    public List<Edge> edges = new ArrayList<Edge>();
    /**
     * stores all vertices aka nodes located in the cluster.
     */
    public List<Vertex> vertices = new ArrayList<Vertex>();
    /**
     * name of the cluster consists of the index and the dimensions.
     */
    public String name;
    /**
     * the index of the cluster.
     */
    public int index;
    /**
     * row of the cluster referring to the whole clustering
     */
    private int row;
    /**
     * col of the cluster referring to the whole clustering.
     */
    private int col;
    /**
     * the height and weight of the cluster.
     */
    private int clusterSize;
    /**
     * clustering of who this cluster is part of.
     */
    private Clustering clustering;

    /**
     * the cluster sides which are already scanned.
     */
    public Set<Aim> scannedAims = new HashSet<Aim>();

    /**
     * initialize a cluster
     * 
     * @param rowIndex
     * @param colIndex
     * @param csize
     * @param idx
     * @param clstering
     */
    public Cluster(int rowIndex, int colIndex, int csize, int idx, Clustering clstering) {
        index = idx;
        clusterSize = csize;
        row = rowIndex;
        col = colIndex;
        clustering = clstering;
        name = "Cluster Idx:" + index + " Dimension R:" + rowIndex * clusterSize + " C:" + colIndex * clusterSize
                + "xR:" + (rowIndex + 1) * clusterSize + " C:" + (colIndex + 1) * clusterSize;
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

    /**
     * print all edges of the cluster into the log file.
     */
    public void debugEdges() {
        LOGGER_PATH.debug("Edges of %s", name);
        LOGGER_PATH.debug("Done_aims_of_%s", scannedAims.toString());
        for (Edge e : edges) {
            LOGGER_PATH.debug("xx: Edge from %s to %s type: %s", e.getTile1(), e.getTile2(), e.getType());
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

    /**
     * connecting two tiles (nodes) with each other by creation a new edge.
     * 
     * @param tStart
     * @param tEnd
     */
    private void findNewEdge(Tile tStart, Tile tEnd) {
        LOGGER.trace("%s: Find new path between %s and %s", name, tStart, tEnd);
        if (tStart.equals(tEnd))
            return;

        Edge edge = new Edge(tStart, tEnd, this, Edge.EdgeType.Intra);
        if (edges.contains(edge))
            return;

        // path limit costs are the manhattanDistance plus a factor for a maybe
        // way around
        int costs = clustering.getPathFinder().getMap().manhattanDistance(tStart, tEnd) * 2;

        Tile searchSpace0 = new Tile(row * clusterSize, col * clusterSize);
        Tile searchSpace1 = new Tile((row + 1) * clusterSize + 1, (col + 1) * clusterSize + 1);
        List<Tile> path = clustering.getPathFinder().search(PathFinder.Strategy.AStar, tStart, tEnd, searchSpace0,
                searchSpace1, costs);

        if (path == null)
            return;

        LOGGER.trace("%s: found!!", name);
        edge.setPath(path);
        edges.add(edge);
        processVertex(edge.getTile1(), edge);
        processVertex(edge.getTile2(), edge);

    }

    /**
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

    /**
     * Converting Aim to EdgeType
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

    /**
     * returns all successor node of a DirectedEdge in the and in the neighbor clusters.
     * 
     * @param e
     *            DirectedEdge
     * @return all successor nodes
     */
    public List<SearchTarget> getEdgeWithNeighbourCluster(DirectedEdge e) {
        List<SearchTarget> list = new ArrayList<SearchTarget>();

        list.addAll(getEdgesOfCluster(e.getEnd(), row, col));

        if (e.getEnd().getRow() == getBottomFrontier()) // south todo wrap
                                                        // around

            list.addAll(getEdgesOfCluster(e.getEnd(), row + 1, col));

        if (e.getEnd().getRow() == getTopFrontier()) // north todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row - 1, col));

        if (e.getEnd().getCol() == getLeftFrontier()) // west todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row, col - 1));

        if (e.getEnd().getCol() == getRightFrontier()) // east todo wrap around

            list.addAll(getEdgesOfCluster(e.getEnd(), row, col + 1));

        LOGGER_ASTAR.debug("%s neighbour cluster found", list.size());
        for (SearchTarget t : list)
            LOGGER_ASTAR.debug("      => %s", t);
        return list;
    }

    /**
     * 
     * @return the col position of left side of the cluster
     */
    private int getLeftFrontier() {
        return col * clusterSize;
    }

    /**
     * 
     * @return the col position of right side of the cluster
     */
    private int getRightFrontier() {

        return (col + 1) * clusterSize;
    }

    /**
     * 
     * @return the row position of top side of the cluster
     */
    private int getTopFrontier() {
        return row * clusterSize;
    }

    /**
     * 
     * @return the row position of bottom side of the cluster
     */
    private int getBottomFrontier() {
        return (row + 1) * clusterSize;
    }

    /**
     * returns all continuative edges of the cluster[clusterRow][clusterCol]
     * 
     * @param start
     * @param clusterRow
     * @param clusterCol
     * @return
     */
    private List<SearchTarget> getEdgesOfCluster(Tile start, int clusterRow, int clusterCol) {
        Cluster c = clustering.getClusterWrapAround(clusterRow, clusterCol);
        LOGGER_PATH.debug("search edge starts with %s in %s", start, c);
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        for (Edge e : c.edges) {
            // LOGGER_PATH.debug("scan edge %s ", e);
            if (e.getTile1().equals(start)) {
                list.add(new DirectedEdge(e.getTile1(), e.getTile2(), c, e.path));
            } else if (e.getTile2().equals(start)) {
                list.add(new DirectedEdge(e.getTile2(), e.getTile1(), c, e.path));
            }
        }
        LOGGER_PATH.debug("edge found %s they are: %s", list.size(), list);
        return list;
    }

    /**
     * 
     * @param checkAim
     * @return true if this aim is already processed.
     */
    public boolean isSideScanned(Aim checkAim) {
        return scannedAims.contains(checkAim);
    }

    /**
     * 
     * @return true if all four side of the cluster ar processed.
     */
    public boolean isClustered() {
        return scannedAims.size() == 4;
    }

    /**
     * the vertices of the new edges are connected with the already existing vertices.
     * 
     * @param newEdges
     * @param newaim
     */
    private void processNewEdgesVertices(List<Edge> newEdges, Aim newaim) {
        for (Edge e : newEdges) {
            e.setEdgeType(getEdgeType(newaim));
            e.setCluster(this);
            // add new vertex to the list if not already exists
            if (processVertex(e.getTile1(), e))
                for (Vertex x : vertices)
                    // vertex is new, we try to connect it with existing vertices
                    findNewEdge(e.getTile1(), x);

            // add new vertex to the list if not already exists
            if (processVertex(e.getTile2(), e))
                // vertex is new, we try to connect it with existing vertices
                for (Vertex x : vertices)
                    findNewEdge(e.getTile2(), x);

        }

    }

    /**
     * add the vertices to the vertices list if they aren't yet.
     * 
     * @param t
     * @param e
     * @return true if the tile is a new vertices and was added
     */
    private boolean processVertex(Tile t, Edge e) {
        if (vertices.contains(t)) {
            if (e != null) {
                Vertex v = vertices.get(vertices.indexOf(t));
                v.addEdge(e);
            }
            return false;
        } else {
            vertices.add(new Vertex(t, e));
            return true;
        }
    }

    /**
     * new scanned edges are integrated into the cluster and are connected with existing edges.
     * 
     * @param newaim
     * @param newEdges
     */
    public void addEdge(Aim newaim, List<Edge> newEdges) {
        scannedAims.add(newaim);
        LOGGER.trace("%s: New edges arrived from aim %s E: %s", name, newaim, newEdges);
        if (newEdges.size() > 0) {
            List<Edge> hardCopy = new ArrayList<Edge>();
            for (Edge e : newEdges) {
                if (edges.contains(e))
                    continue;
                hardCopy.add(new Edge(e.getTile1(), e.getTile2(), e.getPath(), e.getCluster()));

            }
            edges.addAll(hardCopy);
            processNewEdgesVertices(hardCopy, newaim);
            // createNewPath(hardCopy);
            debugEdges();
            if (isClustered()) {
                LOGGER.debug("%s is clustered_now! Vertices: %s Edges: %s", name, vertices.size(), edges.size());
            }
        }

    }

    @Override
    public String toString() {
        return name + " Scanned aims: " + scannedAims + " Edge: " + edges.size() + " Vertices: " + vertices.size();
    }

    /**
     * this method adds new vertices found on a cluster side. they get linked with already existing vertices.
     * 
     * @param sideAim
     *            the side Aim on witch the tiles where calculated
     * @param tiles
     */
    public void addTiles(Aim sideAim, List<Tile> tiles) {
        scannedAims.add(sideAim);
        for (Tile t : tiles) {
            if (processVertex(t, null)) {
                for (Vertex x : vertices)
                    findNewEdge(t, x);
            }
        }
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
