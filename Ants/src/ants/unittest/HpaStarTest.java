package ants.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import ants.entities.Aim;
import ants.entities.DirectedEdge;
import ants.entities.Edge;
import ants.entities.Ilk;
import ants.entities.Tile;
import ants.search.AStarSearchStrategy;
import ants.search.Cluster;
import ants.search.Clustering;
import ants.search.PathFinder;
import ants.state.Ants;
import ants.state.World;
import ants.tasks.ClusteringTask;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class HpaStarTest {

    @Test
    public void BasicTest() {
        Logger.debug(LogCategory.JUNIT, "-----------JUNIT----------- BasicTest init");
        Clustering c = new Clustering(10, 4, 2);
        World w = new World(40, 11, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        Ants.INSTANCE.setup(0, 0, 30, 10, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);

        Edge startEdge = new Edge(new Tile(0, 0), new Tile(10, 0), null);
        c.getClusters()[0][0].SetCluster(Aim.NORTH, Arrays.asList(startEdge));
        c.getClusters()[0][0].SetCluster(Aim.NORTH, Arrays.asList(new Edge(new Tile(0, 0), new Tile(0, 10), null)));
        c.getClusters()[0][0].SetCluster(Aim.SOUTH, Arrays.asList(new Edge(new Tile(10, 0), new Tile(10, 10), null)));
        c.getClusters()[1][0].SetCluster(Aim.NORTH, Arrays.asList(new Edge(new Tile(10, 0), new Tile(10, 10), null)));
        c.getClusters()[1][0].SetCluster(Aim.SOUTH, Arrays.asList(new Edge(new Tile(20, 0), new Tile(20, 10), null)));
        c.getClusters()[2][0].SetCluster(Aim.NORTH, Arrays.asList(new Edge(new Tile(20, 0), new Tile(20, 10), null)));
        Edge endEdge = new Edge(new Tile(30, 0), new Tile(30, 10), null);
        Logger.debug(LogCategory.JUNIT, "JUNIT Addendedge");
        c.getClusters()[2][0].SetCluster(Aim.SOUTH, Arrays.asList(endEdge));

        c.getClusters()[0][0].debugEdges();
        c.getClusters()[1][0].debugEdges();
        c.getClusters()[2][0].debugEdges();

        DirectedEdge e = new DirectedEdge(startEdge.getTile1(), startEdge.getTile2(), c.getClusters()[0][0]);
        DirectedEdge e2 = new DirectedEdge(endEdge.getTile1(), endEdge.getTile2(), c.getClusters()[2][0]);

        Logger.debug(LogCategory.JUNIT, "[0][0] has %s edges", c.getClusters()[0][0].edges.size());
        Logger.debug(LogCategory.JUNIT, "[1][0] has %s edges", c.getClusters()[1][0].edges.size());
        c.getClusters()[1][0].debugEdges();
        Logger.debug(LogCategory.JUNIT, "[2][0] has %s edges", c.getClusters()[2][0].edges.size());

        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest start now!!!");

        PathFinder.bestPath(PathFinder.A_STAR, e, e2, null, null, 50);

        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest ended now!!!");

    }

    @Test
    public void ObstacleTest() {
        Logger.debug(LogCategory.JUNIT, "-----------JUNIT----------- ObstacleTest init");
        /*
         * generate map like this -------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww -----------------------------------------
         * woooooooooowwwwwwwwwwooooooooooooooow -----------------------------------------
         * woooooooooowwwwwwwwwwooooooooooooooow -----------------------------------------
         * woooooooooowwwwwwwwwwooooooooooooooow-----------------------------------------
         * woooooooooowwwwwwwwwwooooooooooooooow -----------------------------------------
         * woooooooooowwwwwwwwwwooooooooooooooow-----------------------------------------
         * wooooooooooooooooooooooooooooooooooow -----------------------------------------
         * wooooooooooooooooooooooooooooooooooow-----------------------------------------
         * wooooooooooooooooooooooooooooooooooow -----------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww-----------------------------------------
         */

        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        w.setWater(new Tile(0, 38), new Tile(40, 40));
        w.setWater(new Tile(38, 0), new Tile(40, 40));
        w.setWater(new Tile(0, 0), new Tile(2, 40));
        w.setWater(new Tile(0, 0), new Tile(40, 2));
        // w.setWater(new Tile(0, 18), new Tile(30, 20));
        // w.setWater(new Tile(20, 10), new Tile(25, 25));
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(8);

        Logger.debug(LogCategory.JUNIT, "ClusteringTask_started now!!!");
        long start = System.currentTimeMillis();

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "ClusteringTask enden Duration: %s milisecods", elapsed);

        Ants.getClusters().getClusters()[0][0].debugEdges();
        Ants.getClusters().getClusters()[1][0].debugEdges();
        Ants.getClusters().getCluster(5).debugEdges();
        Ants.getClusters().getCluster(24).debugEdges();

        Logger.debug(LogCategory.JUNIT, "###HAPAstar pathfinding started now!!!");
        start = System.currentTimeMillis();
        List<Tile> path = PathFinder.bestPath(PathFinder.HPA_STAR, new Tile(5, 5), new Tile(20, 30), null, null, 100);
        elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "HAPAstar pathfinding ended now!!! duration: %s milisecods", elapsed);

        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest ended now!!! path is: %s", path);
        w.debugPathOnMap(path);

    }

    @Test
    public void CompareAtoAstarText() {
        Logger.debug(LogCategory.JUNIT, "-----------JUNIT----------- CompareAtoAstarText init");
        /*
         * generate map like this -------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww-----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow-----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow -----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow-----------------------------------------
         * wooooowwwwwwwwwwwwwwwooooooooooooooow -----------------------------------------
         * wooooowwwwwwwwwwwwwwwwwwwwwoooowwwwww-----------------------------------------
         * wooooowwwoooooooooooooowwwwooooooooow -----------------------------------------
         * wooooowwwoooooooooooooowwwwwwwwooooow-----------------------------------------
         * wooooooooooooowwwoooooowwwwwwwwooooow -----------------------------------------
         * wooooooooooooowwwooooooooooooooooooow-----------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww-----------------------------------------
         */
        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        // border
        w.setWater(new Tile(0, 38), new Tile(40, 40));
        w.setWater(new Tile(38, 0), new Tile(40, 40));
        w.setWater(new Tile(0, 0), new Tile(2, 40));
        w.setWater(new Tile(0, 0), new Tile(40, 2));

        w.setWater(new Tile(0, 15), new Tile(15, 20));
        w.setWater(new Tile(10, 5), new Tile(15, 30));
        w.setWater(new Tile(10, 5), new Tile(25, 7));
        w.setWater(new Tile(29, 6), new Tile(31, 20));
        w.setWater(new Tile(17, 10), new Tile(40, 14));
        w.setWater(new Tile(10, 18), new Tile(20, 20));

        Ants.INSTANCE.setup(0, 0, 30, 10, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(10);
        Tile end =  new Tile(10, 35);
        
        Logger.debug(LogCategory.JUNIT, "ClusteringTask_started now!!!");
        long start = System.currentTimeMillis();

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "ClusteringTask enden Duration: %s milisecods", elapsed);

        Logger.debug(LogCategory.JUNIT, "###HAPAstar pathfinding started now!!!");
        start = System.currentTimeMillis();
        List<Tile> path = PathFinder.bestPath(PathFinder.HPA_STAR, new Tile(5, 5),end, null, null, 100);
        elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "HAPAstar pathfinding ended now!!! duration: %s milisecods", elapsed);

        Logger.debug(LogCategory.JUNIT, "HAPAstar* pathsize: %s path is: %s",path != null ? path.size() : "null", path);
        w.debugPathOnMap(path);
        
        Logger.debug(LogCategory.JUNIT, "### A* pathfinding started now!!!");
        start = System.currentTimeMillis();
        path = PathFinder.bestPath(PathFinder.A_STAR, new Tile(5, 5),end, null, null, 100);
        elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "A* pathfinding ended now!!! duration: %s milisecods", elapsed);

        Logger.debug(LogCategory.JUNIT, "A* path pathsize: %s path is: %s",path != null ? path.size() : "null", path);
        w.debugPathOnMap(path);

    }
    
    
    
    @Test
    public void DifficultObstacleTest() {
        Logger.debug(LogCategory.JUNIT, "-----------JUNIT----------- DifficultObstacleTest init");
        /*
         * generate map like this -------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww-----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow-----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow -----------------------------------------
         * wooooooooooooooowwwwwooooooooooooooow-----------------------------------------
         * wooooowwwwwwwwwwwwwwwooooooooooooooow -----------------------------------------
         * wooooowwwwwwwwwwwwwwwwwwwwwoooowwwwww-----------------------------------------
         * wooooowwwoooooooooooooowwwwooooooooow -----------------------------------------
         * wooooowwwoooooooooooooowwwwwwwwooooow-----------------------------------------
         * wooooooooooooowwwoooooowwwwwwwwooooow -----------------------------------------
         * wooooooooooooowwwooooooooooooooooooow-----------------------------------------
         * wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww-----------------------------------------
         */
        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        // border
        w.setWater(new Tile(0, 38), new Tile(40, 40));
        w.setWater(new Tile(38, 0), new Tile(40, 40));
        w.setWater(new Tile(0, 0), new Tile(2, 40));
        w.setWater(new Tile(0, 0), new Tile(40, 2));

        w.setWater(new Tile(0, 15), new Tile(15, 20));
        w.setWater(new Tile(10, 5), new Tile(15, 20));
        w.setWater(new Tile(10, 5), new Tile(20, 7));
        w.setWater(new Tile(17, 10), new Tile(40, 14));
        w.setWater(new Tile(10, 18), new Tile(20, 20));

        Ants.INSTANCE.setup(0, 0, 30, 10, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(10);

        Logger.debug(LogCategory.JUNIT, "ClusteringTask_started now!!!");
        long start = System.currentTimeMillis();

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "ClusteringTask enden Duration: %s milisecods", elapsed);

        Ants.getClusters().getClusters()[0][0].debugEdges();
        Ants.getClusters().getClusters()[1][0].debugEdges();
        Ants.getClusters().getCluster(5).debugEdges();
       
        Logger.debug(LogCategory.JUNIT, "###HAPAstar pathfinding started now!!!");
        start = System.currentTimeMillis();
        List<Tile> path = PathFinder.bestPath(PathFinder.HPA_STAR, new Tile(7, 8), new Tile(7, 32), null, null, 100);
        elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "HAPAstar pathfinding ended now!!! duration: %s milisecods", elapsed);

        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest ended now!!! path is: %s", path);
        w.debugPathOnMap(path);

    }

}
