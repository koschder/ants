package ants.search;

import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class Clustering {

    private Cluster[][] clusters;
    private int clusterSize = 7;
    private int rows = 0;
    private int cols = 0;

    public Clustering(int cSize) {
        int rows = Ants.getWorld().getRows() / clusterSize + 1;
        int cols = Ants.getWorld().getCols() / clusterSize + 1;
        init(cSize,rows,cols);
    }
    public Clustering(int cSize,int ro, int cl) {
        init(cSize,ro,cl);
    }
    
    
    public void init(int cSize,int ro, int cl) {
        clusterSize = cSize;
        rows = ro;
        cols = cl;
        
        clusters = new Cluster[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                clusters[r][c] = new Cluster(r, c, clusterSize,this);
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
        r = r < 0 ? r+rows : r;
        c = c < 0 ? c+cols : c;
        // Logger.debug(LogCategory.CLUSTERED_ASTAR, "getWithWrapAround row %s col %s wrapped %s:%s %s", row, col, r, c,
        // clusters[r][c]);

        return clusters[r][c];
    }

}
