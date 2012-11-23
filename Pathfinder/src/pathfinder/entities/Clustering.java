package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import api.entities.Aim;
import api.entities.Tile;
import api.map.AbstractWraparoundMap;
import api.map.WorldType;
import api.pathfinder.SearchTarget;

public class Clustering extends AbstractWraparoundMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CLUSTERING);
    private Cluster[][] clusters;
    private int clusterSize = 7;
    private int rows = 0;
    private int cols = 0;
    private int mapRows = 0;
    private int mapCols = 0;
    private SimplePathFinder pathFinder;
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
    public Clustering(SimplePathFinder s, int clusterSize, int mapHeight, int mapWidth) {
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
        return mapRows;
    }

    public int getClusterRows() {
        return rows;
    }

    public int getCols() {
        return mapCols;
    }

    private int getClusterCols() {
        return cols;
    }

    public Cluster[][] getClusters() {
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
    public void updateClusters() {
        for (int r = 0; r < getClusterRows(); r++) {
            for (int c = 0; c < getClusterCols(); c++) {
                LOGGER.trace("Cluster_overview:" + getClusters()[r][c]);
            }
        }
        int updatedClusters = 0;
        int completedClusters = 0;
        for (int r = 0; r < getClusterRows(); r++) {
            for (int c = 0; c < getClusterCols(); c++) {

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

    }

    /**
     * scans the horizontal boarder for tiles or edges to pass
     * 
     * @param r
     * @param c
     * @return
     */
    private boolean scanHorizontalBorder(int r, int c) {
        List<Tile> cornerVertices = new ArrayList<Tile>();
        List<Edge> edges = new ArrayList<Edge>();
        List<Tile> centeredVertices = new ArrayList<Tile>();

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
                    if (path.size() == 1)
                        cornerVertices.add(path.get(0));
                    else if (path.size() > 1)
                        edges.add(new Edge(path));
                    if (path.size() > 0)
                        centeredVertices.add(path.get(path.size() / 2));
                    path = new ArrayList<Tile>();
                }
            } else {
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (path.size() == 1)
            cornerVertices.add(path.get(0));
        else if (path.size() > 1)
            edges.add(new Edge(path));
        if (path.size() > 0)
            centeredVertices.add(path.get(path.size() / 2));

        int neighbourCluster = (r - 1 < 0) ? getClusterRows() - 1 : r - 1;

        if (clusterType == ClusterType.Centered) {
            getClusters()[r][c].addTiles(Aim.NORTH, centeredVertices);
            getClusters()[neighbourCluster][c].addTiles(Aim.SOUTH, centeredVertices);
        } else {
            getClusters()[r][c].addEdge(Aim.NORTH, edges);
            getClusters()[r][c].addTiles(Aim.NORTH, cornerVertices);
            getClusters()[neighbourCluster][c].addEdge(Aim.SOUTH, edges);
            getClusters()[neighbourCluster][c].addTiles(Aim.SOUTH, cornerVertices);
        }
        return true;
    }

    private boolean scanVerticalBorder(int r, int c) {
        List<Tile> centeredVertices = new ArrayList<Tile>();
        List<Edge> edges = new ArrayList<Edge>();
        List<Tile> cornerVertices = new ArrayList<Tile>();

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
                    if (path.size() == 1)
                        cornerVertices.add(path.get(0));
                    else if (path.size() > 1)
                        edges.add(new Edge(path));
                    if (path.size() > 0)
                        centeredVertices.add(path.get(path.size() / 2));
                    path = new ArrayList<Tile>();
                }
            } else {
                LOGGER.trace("clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return false;
            }
        }
        if (path.size() == 1)
            cornerVertices.add(path.get(0));
        else if (path.size() > 1)
            edges.add(new Edge(path));
        if (path.size() > 0)
            centeredVertices.add(path.get(path.size() / 2));

        int neighbourCluster = (c - 1 < 0) ? getClusterCols() - 1 : c - 1;
        if (clusterType == ClusterType.Centered) {
            getClusters()[r][c].addTiles(Aim.WEST, centeredVertices);
            getClusters()[r][neighbourCluster].addTiles(Aim.EAST, centeredVertices);
        } else {
            getClusters()[r][c].addTiles(Aim.WEST, cornerVertices);
            getClusters()[r][c].addEdge(Aim.WEST, edges);
            getClusters()[r][neighbourCluster].addTiles(Aim.EAST, cornerVertices);
            getClusters()[r][neighbourCluster].addEdge(Aim.EAST, edges);
        }

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

    /**
     * 
     * @return the current pathfinder
     */
    protected SimplePathFinder getPathFinder() {
        return pathFinder;
    }

    public List<Tile> getAllVertices() {
        List<Tile> verts = new ArrayList<Tile>();
        for (Cluster[] cs : getClusters())
            for (Cluster c : cs) {
                System.out.println("cluster: " + c.name);
                for (Vertex v : c.vertices) {
                    verts.add(v.getTargetTile());
                }
            }
        return verts;
    }

    public void setWorldType(WorldType t) {
        // TODO
    }

    public void printVertices() {
        for (Cluster[] cs : getClusters())
            for (Cluster c : cs) {
                System.out.println("cluster: " + c.name);
                for (Vertex v : c.vertices) {
                    System.out.println(v);
                }
            }
    }

    @Override
    public List<SearchTarget> getSuccessor(SearchTarget currentEdge, boolean isNextMove) {
        if (!(currentEdge instanceof DirectedEdge)) {
            throw new IllegalArgumentException("SearchTarget must be of the type DirectedEdge");
        }

        DirectedEdge e = (DirectedEdge) currentEdge;

        return e.getCluster().getEdgeWithNeighbourCluster(e);
    }

    @Override
    public boolean isPassable(Tile tile) {
        return true;
    }

    @Override
    public boolean isVisible(Tile tile) {
        return true;
    }

    public void printEdges() {
        for (Cluster[] cs : getClusters())
            for (Cluster c : cs) {
                System.out.println("cluster: " + c.name);
                for (Edge e : c.edges) {
                    System.out.println(String.format("C: %s Edge: %s PathLength:%s %s", c.index, e,
                            e.path == null ? "Null" : e.path.size(), e.path == null ? "Null" : e.path));
                }
            }
    }

}
