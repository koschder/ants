package ants.tasks;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Aim;
import ants.entities.Edge;
import ants.entities.Tile;
import ants.search.Cluster;
import ants.search.Clustering;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class ClusteringTask implements Task {

    Clustering clusters = Ants.getClusters();

    @Override
    public void perform() {

        for (int r = 0; r < clusters.getRows(); r++) {
            for (int c = 0; c < clusters.getCols(); c++) {
                Logger.debug(LogCategory.CLUSTERING_Detail, "Cluster_overview:" + clusters.getClusters()[r][c]);
            }
        }

        int updatedClusters = 0;
        int completedClusters = 0;
        for (int r = 0; r < clusters.getRows(); r++) {
            for (int c = 0; c < clusters.getCols(); c++) {

                if (clusters.getClusters()[r][c].isClustered()) {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Cluster is clustered %s", clusters.getClusters()[r][c]);
                    completedClusters++;
                    continue;
                }
                // if (!clusters.getClusters()[r][c].isFullVisible())
                // continue;

                Cluster current = clusters.getClusters()[r][c];

                if (!current.hasScan(Aim.WEST)) {
                    List<Edge> edgesV = verticalScan(r, c, clusters.getClusterSize());
                    if (edgesV != null) {
                        clusters.getClusters()[r][c].SetCluster(Aim.WEST, edgesV);
                        int cNeighbour = (c - 1 < 0) ? clusters.getCols() - 1 : c - 1;
                        clusters.getClusters()[r][cNeighbour].SetCluster(Aim.EAST, edgesV);
                        updatedClusters++;        
                    }
                } else {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Already clustered in Aim %s on r: %s c: %s", Aim.WEST,
                            r, c);
                }

                if (!current.hasScan(Aim.NORTH)) {
                    List<Edge> edgesH = horizontalScan(r, c, clusters.getClusterSize());
                    if (edgesH != null) {
                        clusters.getClusters()[r][c].SetCluster(Aim.NORTH, edgesH);
                        int rNeighbour = (r - 1 < 0) ? clusters.getRows() - 1 : r - 1;
                        try {
                            clusters.getClusters()[rNeighbour][c].SetCluster(Aim.SOUTH, edgesH);
                        } catch (Exception ex) {
                            System.out.println(r + " " + rNeighbour + " " + c);
                        }
                        updatedClusters++;
                    }
                } else {
                    Logger.debug(LogCategory.CLUSTERING_Detail, "Already clustered in Aim %s on r: %s c: %s",
                            Aim.NORTH, r, c);
                }
                current.debugEdges();
            }
        }
        Logger.debug(LogCategory.CLUSTERING, "Clusters updated: %s", updatedClusters);
        Logger.debug(LogCategory.CLUSTERING, "Clusters compeleted: %s", completedClusters);

        for (int r = 0; r < clusters.getRows(); r++) {
            for (int c = 0; c < clusters.getCols(); c++) {
                Logger.debug(LogCategory.CLUSTERING, clusters.getClusters()[r][c].toString());
            }
        }
    }

    private List<Edge> horizontalScan(int r, int c, int clusterSize) {
        List<Edge> edges = new ArrayList<Edge>();
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = Math.max(0, r * clusterSize % Ants.getWorld().getRows());
        int startColTile = c * clusterSize;

        // whole cluster or shorten if we are on the "end" of the grid
        int endColTile = Math.min(Ants.getWorld().getCols(), (c + 1) * clusterSize);

        Logger.debug(LogCategory.CLUSTERING_Detail, "horizontalScan on r: %s c: %s to r: %s c: %s", startRowTile,
                startColTile, startRowTile, startColTile + clusterSize);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startColTile; i < endColTile; i++) {
            Tile tile = new Tile(startRowTile, i);

            Logger.debug(LogCategory.CLUSTERING_Detail, "Check %s", tile);
            if (Ants.getWorld().isVisible(tile)) {
                if (Ants.getWorld().getIlk(tile).isPassable()) {

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
                        edges.add(new Edge(vertices, lastVertices,path, null));
                       // path = new ArrayList<Tile>();
                    }
                    vertices = null;
                    lastVertices = null;
                }

            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return null;
            }
        }
        if (vertices != null && lastVertices != null) {
           
            edges.add(new Edge(vertices, lastVertices,path, null));
        } else if (vertices != null) {
            // todo what to do?
        }
        return edges;
    }

    public List<Edge> verticalScan(int r, int c, int clusterSize) {
        List<Edge> edges = new ArrayList<Edge>();
        // left
        Tile vertices = null;
        Tile lastVertices = null;
        int startRowTile = r * clusterSize;
        int startColTile = Math.max(0, c * clusterSize % Ants.getWorld().getCols());
        // whole cluster or shorten if we are on the "end" of the grid
        int endRowTile = Math.min(Ants.getWorld().getRows(), (r + 1) * clusterSize);

        Logger.debug(LogCategory.CLUSTERING_Detail, "verticalScan on r: %s c: %s to r: %s c: %s", startRowTile,
                startColTile, startRowTile + clusterSize, startColTile);
        List<Tile> path = new ArrayList<Tile>();
        for (int i = startRowTile; i < endRowTile; i++) {
            Tile tile = new Tile(i, startColTile);
            Logger.debug(LogCategory.CLUSTERING_Detail, "Check %s", tile);
            
            if (Ants.getWorld().isVisible(tile)) {
                if (Ants.getWorld().getIlk(tile).isPassable()) {

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

                        edges.add(new Edge(vertices, lastVertices,path, null));
                       // path = new ArrayList<Tile>();
                    }
                    vertices = null;
                    lastVertices = null;
                }

            } else {
                Logger.debug(LogCategory.CLUSTERING_Detail,
                        "clustering breaked not all surrounding tiles are visible cluster r %s c %s", r, c);
                return null;
            }
        }
        if (vertices != null && lastVertices != null) {           
            edges.add(new Edge(vertices, lastVertices, path,null));
        } else if (vertices != null) {
            // todo what to do?
        }
        return edges;
    }

    @Override
    public void setup() {
        // TODO Auto-generated method stub

    }

}
