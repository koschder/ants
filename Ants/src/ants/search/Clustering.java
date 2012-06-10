package ants.search;

import java.util.List;

import ants.entities.Aim;
import ants.entities.DirectedEdge;
import ants.entities.Edge;
import ants.entities.Tile;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Clustering {

    private Cluster[][] clusters;
    private int clusterSize = 7;
    private int rows = 0;
    private int cols = 0;

    public Clustering(int clusterSiz) {
        
        float size = clusterSiz;
        
        int r = (int) Math.ceil(Ants.getWorld().getRows() / size);
        int c = (int) Math.ceil(Ants.getWorld().getCols() / size);
        init(clusterSiz, r, c);
    }

    public Clustering(int cSize, int ro, int cl) {
        init(cSize, ro, cl);
    }

    public void init(int cSize, int ro, int cl) {
        clusterSize = cSize;
        rows = ro;
        cols = cl;

        clusters = new Cluster[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                clusters[r][c] = new Cluster(r, c, clusterSize,r*cols+c, this);
            }
        }

        Logger.debug(LogCategory.CLUSTERING, "Cluster_array %s x %s initialised", rows, cols);

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

    public Cluster getWithWrapAround(int row, int col) {

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

        List<Aim> aims = Ants.getWorld().getDirections(start, end);
        // try to find a cluster edge in direction to target
        for (Aim a : aims) {
            if (c.hasScan(a)) {
                Edge e = c.getEdgeOnBoarder(a);
                
                if (e != null){
                List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, start, e.getTile1(), clusterSize*2);
                    // there is a path to the cluster boarder
                    if(path != null){
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
                if (e != null){
                    List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, start, e.getTile1(), clusterSize*2);
                        // there is a path to the cluster boarder
                        if(path != null){
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
        
        return getWithWrapAround(start.getRow()/clusterSize, start.getCol()/clusterSize);
    }

    public Cluster getCluster(int iId) {
        int row = (int)iId / cols;
        int col = (int)iId % cols;
        return clusters[row][col];
    }
}
