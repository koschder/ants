package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import api.Aim;
import api.Tile;
import api.WorldType;

public class Clustering {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CLUSTERING);
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

    /***
     * Initializes the clustering.
     * 
     * @param s
     * @param clusterSize
     * @param mapHeight
     * @param mapWidth
     */
    public Clustering(PathFinder s, int clusterSize, int mapHeight, int mapWidth) {
        this.pathFinder = s;
        this.clusterType = ClusterType.Corner;
        init(clusterSize, mapHeight, mapWidth);
    }

    /***
     * Initializes all the clusters.
     * 
     * @param clusterSize
     * @param mapHeight
     * @param mapWidth
     */
    protected void init(int clusterSize, double mapHeight, double mapWidth) {
        this.clusterSize = clusterSize;
        mapRows = (int) mapHeight;
        mapCols = (int) mapWidth;
        rows = (int) Math.ceil(mapHeight / clusterSize);
        cols = (int) Math.ceil(mapWidth / clusterSize);

        clusters = new Cluster[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                clusters[r][c] = new Cluster(r, c, clusterSize, r * cols + c, this);
            }
        }

        LOGGER.debug("Cluster_array %s x %s initialised", rows, cols);

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

    protected Cluster[][] getClusters() {
        return clusters;
    }

    /***
     * returns the cluster considering if the cluster is wrapped around.
     * 
     * @param row
     * @param col
     * @return the cluster or null if nothing found
     */
    protected Cluster getClusterWrapAround(int row, int col) {
        int r = row % rows;
        int c = col % cols;
        r = r < 0 ? r + rows : r;
        c = c < 0 ? c + cols : c;
        return clusters[r][c];
    }

    /***
     * Returns a edge in the cluster witch has a passable path the the start tile and is most suitable in direction to
     * the end tile.
     * 
     * @param start
     * @param end
     * @return the start edge with whom we can start hpastar
     */
    public DirectedEdge getStartEdge(Tile start, Tile end) {
        Cluster c = getClusterOf(start);
        if (c == null)
            return null;

        // we dont need to check if there is a path between start and e.getTile2() because if there is no between start
        // and e.getTile1()
        // there will be no between start and e.getTile2() as well.
        for (Edge e : c.getEdges()) {
            List<Tile> path = pathFinder.search(PathFinder.Strategy.AStar, start, e.getTile1(), clusterSize * 2);
            if (path != null) {
                DirectedEdge e1 = new DirectedEdge(start, e.getTile1(), c);
                e1.setPath(path);
                return e1;
            }
        }

        // List<Aim> suitableAims = pathFinder.getMap().getDirections(start, end);
        // // try to find a cluster edge in direction to target (end)
        // for (Aim side : suitableAims) {
        // if (c.isSideScanned(side)) {
        // Edge e = c.getEdgeOnBoarder(side);
        // if (e != null) {
        // List<Tile> path = pathFinder
        // .search(PathFinder.Strategy.AStar, start, e.getTile1(), clusterSize * 2);
        //
        // // there is a path to the cluster boarder
        // if (path != null) {
        // DirectedEdge de = new DirectedEdge(start, e.getTile2(), c);
        // de.setPath(path);
        // return de;
        // }
        // }
        // }
        // }
        // for (Aim a : c.getAims()) {
        // if (c.isSideScanned(a) && !suitableAims.contains(a)) {
        // Edge e = c.getEdgeOnBoarder(a);
        // if (e != null) {
        // List<Tile> path = pathFinder
        // .search(PathFinder.Strategy.AStar, start, e.getTile1(), clusterSize * 2);
        // // there is a path to the cluster border
        // if (path != null) {
        // DirectedEdge de = new DirectedEdge(start, e.getTile1(), c);
        // de.setPath(path);
        // return de;
        // }
        // }
        // }
        // }
        return null;
    }

    /***
     * calculates the cluster of who the tile is part of, and returns the cluster.
     * 
     * @param start
     * @return cluster of who the tile is part of
     */
    private Cluster getClusterOf(Tile start) {
        return getClusterWrapAround(start.getRow() / clusterSize, start.getCol() / clusterSize);
    }

    /***
     * start clustering the map
     */
    public void perform() {
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                LOGGER.trace("Cluster_overview:" + getClusters()[r][c]);
            }
        }
        int updatedClusters = 0;
        int completedClusters = 0;
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {

                if (getClusters()[r][c].isClustered()) {
                    LOGGER.trace("Cluster is clustered %s", getClusters()[r][c]);
                    completedClusters++;
                    continue;
                }
                Cluster current = getClusters()[r][c];

                if (!current.isSideScanned(Aim.WEST)) {
                    if (scanVerticalBorder(r, c))
                        updatedClusters++;
                } else {
                    LOGGER.trace("Already clustered in Aim %s on r: %s c: %s", Aim.WEST, r, c);
                }

                if (!current.isSideScanned(Aim.NORTH)) {
                    if (scanHorizontalBorder(r, c))
                        updatedClusters++;
                } else {
                    LOGGER.trace("Already clustered in Aim %s on r: %s c: %s", Aim.NORTH, r, c);
                }
                // current.debugEdges();
            }
        }
        LOGGER.debug("Clusters updated: %s", updatedClusters);
        LOGGER.debug("Clusters compeleted: %s", completedClusters);
        // for (int r = 0; r < getRows(); r++) {
        // for (int c = 0; c < getCols(); c++) {
        // LOGGER.debug(getClusters()[r][c].toString());
        // }
        // }
    }

    /***
     * Scans the North border line
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean scanVerticalBorder(int r, int c) {
        if (clusterType == ClusterType.Corner)
            return verticalScan_Corners(r, c);

        if (clusterType == ClusterType.Centered)
            return verticalScan_Centered(r, c);

        throw new RuntimeException("ClusterType not implemented " + clusterType);
    }

    /***
     * Scans the East border line
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean scanHorizontalBorder(int r, int c) {

        if (clusterType == ClusterType.Corner)
            return horizontalScan_Corners(r, c);

        if (clusterType == ClusterType.Centered)
            return horizontalScan_Centered(r, c);

        throw new RuntimeException("ClusterType not implemented " + clusterType);
    }

    /***
     * Scans a horizontal border an sets the connecting vertices on the corners
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean horizontalScan_Corners(int r, int c) {
        List<Edge> edges = new ArrayList<Edge>();
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = Math.max(0, r * clusterSize % pathFinder.getMap().getRows());
        int startColTile = c * clusterSize;

        // whole cluster or shorten if we are on the "end" of the grid
        int endColTile = Math.min(pathFinder.getMap().getCols(), (c + 1) * clusterSize);

        LOGGER.trace("horizontalScan on r: %s c: %s to r: %s c: %s", startRowTile, startColTile, startRowTile,
                startColTile + clusterSize);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startColTile; i <= endColTile; i++) {
            Tile tile = getWrapAroundTile(startRowTile, i);

            LOGGER.trace("Check %s", tile);
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
                    LOGGER.trace("Not passable %s", tile);
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
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
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

    /***
     * Scans a vertical border an sets the connecting vertices on the corners
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean verticalScan_Corners(int r, int c) {
        List<Edge> edges = new ArrayList<Edge>();
        // left
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = r * clusterSize;
        int startColTile = Math.max(0, c * clusterSize % pathFinder.getMap().getCols());
        // whole cluster or shorten if we are on the "end" of the grid
        int endRowTile = Math.min(pathFinder.getMap().getRows(), (r + 1) * clusterSize);

        LOGGER.trace("verticalScan on r: %s c: %s to r: %s c: %s", startRowTile, startColTile, startRowTile
                + clusterSize, startColTile);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startRowTile; i <= endRowTile; i++) {
            Tile tile = getWrapAroundTile(i, startColTile);
            LOGGER.trace("Check %s", tile);

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
                    LOGGER.trace("Not passable %s", tile);// tileNeighbour);
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
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (vertices != null && lastVertices != null) {
            edges.add(new Edge(vertices, lastVertices, path, null));
        } else if (vertices != null) {
            // todo what to do?
        }

        getClusters()[r][c].addEdge(Aim.WEST, edges);
        int cNeighbour = (c - 1 < 0) ? getCols() - 1 : c - 1;
        getClusters()[r][cNeighbour].addEdge(Aim.EAST, edges);
        return true;

    }

    /***
     * Scans a horizontal border an stores every center vertex of each passage connecting a neighbour cluster
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean horizontalScan_Centered(int r, int c) {
        List<Tile> tiles = new ArrayList<Tile>();
        int startRowTile = Math.max(0, r * clusterSize % pathFinder.getMap().getRows());
        int startColTile = c * clusterSize;
        // whole cluster or shorten if we are on the "end" of the grid
        int endColTile = Math.min(pathFinder.getMap().getCols(), (c + 1) * clusterSize);

        List<Tile> path = new ArrayList<Tile>();
        for (int i = startColTile; i <= endColTile; i++) {
            Tile tile = getWrapAroundTile(startRowTile, i);
            LOGGER.trace("Check %s", tile);
            if (pathFinder.getMap().isVisible(tile)) {
                if (pathFinder.getMap().isPassable(tile)) {
                    path.add(tile);
                } else { // now blocked add the last start and endpoint to cluster.
                    if (path.size() > 0)
                        tiles.add(path.get(path.size() / 2));
                    path = new ArrayList<Tile>();
                }
            } else {
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
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

    /***
     * Scans a vertical border an stores every center vertex of each passage connecting a neighbour cluster
     * 
     * @param r
     * @param c
     * @return true if scanning successful, false if not all part of the scanned line was visible
     */
    private boolean verticalScan_Centered(int r, int c) {
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
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
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

    /***
     * 
     * @param r
     *            row
     * @param c
     *            column
     * @return a tile considering it the parameters must be fit to the world size
     */
    private Tile getWrapAroundTile(int r, int c) {

        return new Tile(r % mapRows, c % mapCols);
    }

    /***
     * 
     * @return the current pathfinder
     */
    protected PathFinder getPathFinder() {
        return pathFinder;
    }

    // public List<SearchTarget> getSuccessors(DirectedEdge state) {
    // List<SearchTarget> list = new ArrayList<SearchTarget>();
    // if (state.getCluster() == null)
    // return list;
    // list.addAll(state.getCluster().getEdgeWithNeighbourCluster(state));
    // return list;
    // }

    public List<Tile> getAllVertices() {
        List<Tile> verts = new ArrayList<Tile>();
        for (Cluster[] cs : getClusters())
            for (Cluster c : cs) {
                System.out.println("clsuter: " + c.name);
                for (Vertex v : c.vertices) {
                    verts.add(v.getTargetTile());
                }
            }
        return verts;
    }

    public void setWorldType(WorldType t) {
        // TODO
    }

}
