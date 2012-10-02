package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

import pathfinder.PathFinder;
import pathfinder.PathFinder.WorldType;

import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Clustering {

    private Cluster[][] clusters;
    private int clusterSize = 7;
    private int rows = 0;
    private int cols = 0;
    private int mapRows = 0;
    private int mapCols = 0;
    private PathFinder pathFinder;
    private ClusterType clusterType;

    public enum ClusterType {
        Centered,
        Corner
    };

    public Clustering(PathFinder s, int cSize, int ro, int cl) {
        this.pathFinder = s;
        this.clusterType = ClusterType.Corner;
        init(cSize, ro, cl);
    }

    public void init(int cSize, double ro, double cl) {
        clusterSize = cSize;
        mapRows = (int) ro;
        mapCols = (int) cl;
        rows = (int) Math.ceil(ro / cSize);
        cols = (int) Math.ceil(cl / cSize);

        clusters = new Cluster[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                clusters[r][c] = new Cluster(r, c, clusterSize, r * cols + c, this);
            }
        }

        Logger.debug(LogCategory.CLUSTERING, "Cluster_array %s x %s initialised", rows, cols);

    }

    public void setClusterType(ClusterType t) {
        clusterType = t;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public Cluster[][] getClusters() {
        return clusters;
    }

    public Cluster getClusterWrapAround(int row, int col) {

        int r = row % rows;
        int c = col % cols;
        r = r < 0 ? r + rows : r;
        c = c < 0 ? c + cols : c;
        // Logger.debug(LogCategory.CLUSTERED_ASTAR, "getWithWrapAround row %s col %s wrapped %s:%s %s", row, col, r, c,
        // clusters[r][c]);

        return clusters[r][c];
    }

    public DirectedEdge getStartEdge(Tile start, Tile end) {

        Cluster c = getClusterOf(start);
        if (c == null)
            return null;

        List<Aim> aims = pathFinder.getMap().getDirections(start, end);
        // try to find a cluster edge in direction to target
        for (Aim a : aims) {
            if (c.hasScan(a)) {
                Edge e = c.getEdgeOnBoarder(a);

                if (e != null) {

                    List<Tile> path = pathFinder
                            .search(PathFinder.Strategy.AStar, start, e.getTile1(), clusterSize * 2);

                    // there is a path to the cluster boarder
                    if (path != null) {
                        DirectedEdge de = new DirectedEdge(start, e.getTile2(), c);
                        de.setPath(path);
                        return de;
                    }
                }
            }
        }
        for (Aim a : c.getAims()) {
            if (c.hasScan(a)) {
                Edge e = c.getEdgeOnBoarder(a);
                // todo take v1 or v2?
                if (e != null) {
                    List<Tile> path = pathFinder
                            .search(PathFinder.Strategy.AStar, start, e.getTile1(), clusterSize * 2);
                    // there is a path to the cluster boarder
                    if (path != null) {
                        DirectedEdge de = new DirectedEdge(start, e.getTile1(), c);
                        de.setPath(path);
                        return de;
                    }
                }
            }
        }

        return null;
    }

    private Cluster getClusterOf(Tile start) {

        return getClusterWrapAround(start.getRow() / clusterSize, start.getCol() / clusterSize);
    }

    public Cluster getCluster(int iId) {
        int row = (int) iId / cols;
        int col = (int) iId % cols;
        return clusters[row][col];
    }

    /***
     * start clustering the map
     */
    public void perform() {

        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                Logger.debug(LogCategory.CLUSTERING_Detail, "Cluster_overview:" + getClusters()[r][c]);
            }
        }

        int updatedClusters = 0;
        int completedClusters = 0;
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {

                if (getClusters()[r][c].isClustered()) {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Cluster is clustered %s", getClusters()[r][c]);
                    completedClusters++;
                    continue;
                }
                Cluster current = getClusters()[r][c];

                if (!current.hasScan(Aim.WEST)) {
                    if (scanVerticalBorder(r, c))
                        updatedClusters++;
                } else {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Already clustered in Aim %s on r: %s c: %s", Aim.WEST,
                            r, c);
                }

                if (!current.hasScan(Aim.NORTH)) {
                    if (scanHorizontalBorder(r, c))
                        updatedClusters++;
                } else {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Already clustered in Aim %s on r: %s c: %s",
                            Aim.NORTH, r, c);
                }
                current.debugEdges();
            }
        }
        Logger.debug(LogCategory.CLUSTERING, "Clusters updated: %s", updatedClusters);
        Logger.debug(LogCategory.CLUSTERING, "Clusters compeleted: %s", completedClusters);

        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                Logger.debug(LogCategory.CLUSTERING, getClusters()[r][c].toString());
            }
        }
    }

    private boolean scanVerticalBorder(int r, int c) {
        if (clusterType == ClusterType.Corner)
            return verticalScan_Corners(r, c);

        if (clusterType == ClusterType.Centered)
            return verticalScan_Centred(r, c);

        throw new RuntimeException("ClusterType not implemented " + clusterType);
    }

    private boolean scanHorizontalBorder(int r, int c) {

        if (clusterType == ClusterType.Corner)
            return horizontalScan_Corners(r, c);

        if (clusterType == ClusterType.Centered)
            return horizontalScan_Centred(r, c);

        throw new RuntimeException("ClusterType not implemented " + clusterType);
    }

    private boolean horizontalScan_Corners(int r, int c) {
        List<Edge> edges = new ArrayList<Edge>();
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = Math.max(0, r * clusterSize % pathFinder.getMap().getRows());
        int startColTile = c * clusterSize;

        // whole cluster or shorten if we are on the "end" of the grid
        int endColTile = Math.min(pathFinder.getMap().getCols(), (c + 1) * clusterSize);

        Logger.debug(LogCategory.CLUSTERING_Detail, "horizontalScan on r: %s c: %s to r: %s c: %s", startRowTile,
                startColTile, startRowTile, startColTile + clusterSize);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startColTile; i <= endColTile; i++) {
            Tile tile = getWrapAroundTile(startRowTile, i);

            Logger.debug(LogCategory.CLUSTERING_Detail, "Check %s", tile);
            if (pathFinder.getMap().isVisible(tile)) {
                if (pathFinder.getMap().isPassable(tile)) {
                    if (vertices == null) {
                        vertices = tile;
                        path.add(tile);
                    } else {
                        path.add(tile);
                        lastVertices = tile;
                    }
                } else { // now blocked add the last start and endpoint to cluster.
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Not passable %s", tile);
                    if (lastVertices == null) {
                        // todo what to do?
                    } else {
                        edges.add(new Edge(vertices, lastVertices, path, null));
                        // path = new ArrayList<Tile>();
                    }
                    vertices = null;
                    lastVertices = null;
                }

            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (vertices != null && lastVertices != null) {

            edges.add(new Edge(vertices, lastVertices, path, null));
        } else if (vertices != null) {
            // todo what to do?
        }
        getClusters()[r][c].addEdge(Aim.NORTH, edges);
        int rNeighbour = (r - 1 < 0) ? getRows() - 1 : r - 1;
        try {
            getClusters()[rNeighbour][c].addEdge(Aim.SOUTH, edges);
            return true;
        } catch (Exception ex) {
            System.out.println(r + " " + rNeighbour + " " + c);
        }
        return false;
    }

    private boolean verticalScan_Corners(int r, int c) {
        List<Edge> edges = new ArrayList<Edge>();
        // left
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = r * clusterSize;
        int startColTile = Math.max(0, c * clusterSize % pathFinder.getMap().getCols());
        // whole cluster or shorten if we are on the "end" of the grid
        int endRowTile = Math.min(pathFinder.getMap().getRows(), (r + 1) * clusterSize);

        Logger.debug(LogCategory.CLUSTERING_Detail, "verticalScan on r: %s c: %s to r: %s c: %s", startRowTile,
                startColTile, startRowTile + clusterSize, startColTile);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startRowTile; i <= endRowTile; i++) {
            Tile tile = getWrapAroundTile(i, startColTile);
            Logger.debug(LogCategory.CLUSTERING_Detail, "Check %s", tile);

            if (pathFinder.getMap().isVisible(tile)) {
                if (pathFinder.getMap().isPassable(tile)) {
                    if (vertices == null) {
                        vertices = tile;
                        path.add(tile);
                    } else {
                        path.add(tile);
                        lastVertices = tile;
                    }
                } else { // now blocked add the last start and endpoint to cluster.
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Not passable %s", tile);// tileNeighbour);
                    if (lastVertices == null) {
                        // todo what to do?
                    } else {

                        edges.add(new Edge(vertices, lastVertices, path, null));
                        // path = new ArrayList<Tile>();
                    }
                    vertices = null;
                    lastVertices = null;
                }

            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (vertices != null && lastVertices != null) {
            edges.add(new Edge(vertices, lastVertices, path, null));
        } else if (vertices != null) {
            // todo what to do?
        }

        if (edges != null) {
            getClusters()[r][c].addEdge(Aim.WEST, edges);
            int cNeighbour = (c - 1 < 0) ? getCols() - 1 : c - 1;
            getClusters()[r][cNeighbour].addEdge(Aim.EAST, edges);
            return true;
        }
        return false;
    }

    private boolean horizontalScan_Centred(int r, int c) {
        List<Tile> tiles = new ArrayList<Tile>();
        int startRowTile = Math.max(0, r * clusterSize % pathFinder.getMap().getRows());
        int startColTile = c * clusterSize;
        // whole cluster or shorten if we are on the "end" of the grid
        int endColTile = Math.min(pathFinder.getMap().getCols(), (c + 1) * clusterSize);

        List<Tile> path = new ArrayList<Tile>();
        for (int i = startColTile; i <= endColTile; i++) {
            Tile tile = getWrapAroundTile(startRowTile, i);
            Logger.debug(LogCategory.CLUSTERING_Detail, "Check %s", tile);
            if (pathFinder.getMap().isVisible(tile)) {
                if (pathFinder.getMap().isPassable(tile)) {
                    path.add(tile);
                } else { // now blocked add the last start and endpoint to cluster.
                    if (path.size() > 0)
                        tiles.add(path.get(path.size() / 2));
                    path = new ArrayList<Tile>();
                }
            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (path.size() > 0)
            tiles.add(path.get(path.size() / 2));

        int neighbourCluster = (r - 1 < 0) ? getRows() - 1 : r - 1;
        getClusters()[r][c].addTiles(Aim.NORTH, tiles);
        getClusters()[neighbourCluster][c].addTiles(Aim.SOUTH, tiles);
        return true;
    }
    
    private boolean verticalScan_Centred(int r, int c) {
        List<Tile> tiles = new ArrayList<Tile>();
        int startRowTile = r * clusterSize;
        int startColTile = Math.max(0, c * clusterSize % pathFinder.getMap().getCols());
        // whole cluster or shorten if we are on the "end" of the grid
        int endRowTile = Math.min(pathFinder.getMap().getRows(), (r + 1) * clusterSize);

        List<Tile> path = new ArrayList<Tile>();
        for (int i = startRowTile; i <= endRowTile; i++) {
            Tile tile = getWrapAroundTile(i, startColTile);
            if (pathFinder.getMap().isVisible(tile)) {
                if (pathFinder.getMap().isPassable(tile)) {
                    path.add(tile);
                } else { // now blocked add the last start and endpoint to cluster.
                    if (path.size() > 0)
                        tiles.add(path.get(path.size() / 2));
                    path = new ArrayList<Tile>();
                }
            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (path.size() > 0)
            tiles.add(path.get(path.size() / 2));

        int neighbourCluster = (c - 1 < 0) ? getCols() - 1 : c - 1;
        getClusters()[r][c].addTiles(Aim.WEST, tiles);
        getClusters()[r][neighbourCluster].addTiles(Aim.EAST, tiles);
        return true;

    }

    private Tile getWrapAroundTile(int r, int c) {

        return new Tile(r % mapRows, c % mapCols);
    }

    public PathFinder getPathFinder() {
        return pathFinder;
    }

    public List<SearchTarget> getSuccessors(DirectedEdge state) {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if (state.getCluster() == null)
            return list;
        list.addAll(state.getCluster().getEdgeWithNeighbourCluster(state));
        return list;
    }

    public List<Vertex> getAllVertices() {
        List<Vertex> verts = new ArrayList<Vertex>();
        for (Cluster[] cs : getClusters())
            for (Cluster c : cs) {
                System.out.println("clsuter: " + c.name);
//                for (Vertex x : c.vertices) {
//                    System.out.println("V: " + x);
//                }
                verts.addAll(c.vertices);
            }
        return verts;
    }

    public void setWorldType(WorldType t) {
        //
    }

}
