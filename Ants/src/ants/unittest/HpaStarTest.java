package ants.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

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
        Logger.debug(LogCategory.JUNIT, "JUNIT ObstacleTestTest init");
        /*
         * generate map like this 
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
//        w.setWater(new Tile(0, 38), new Tile(40, 40));
//        w.setWater(new Tile(38, 0), new Tile(40, 40));
//        w.setWater(new Tile(0, 0), new Tile(2, 40));
//        w.setWater(new Tile(0, 0), new Tile(40, 2));
//        w.setWater(new Tile(0, 10), new Tile(20, 20));
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(8);

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        
        int amount = Ants.INSTANCE.getClusters().getRows()  * Ants.INSTANCE.getClusters().getCols(); 
        for(int i = 0;i < amount;i++ ){
            System.out.println("Idx: "+i+" is clustered "+Ants.INSTANCE.getClusters().getCluster(i).isClustered());
            Assert.assertTrue(Ants.INSTANCE.getClusters().getCluster(i).isClustered());
        }
        
        Ants.getClusters().getClusters()[0][0].debugEdges();
        Ants.getClusters().getClusters()[1][0].debugEdges();
        Ants.getClusters().getCluster(5).debugEdges();
        Ants.getClusters().getCluster(24).debugEdges();
        
        Logger.debug(LogCategory.JUNIT, "JUNIT ObstacleTestTest hpastar");
        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest start now!!! ##############################");
        
        List<Tile> path =  PathFinder.bestPath(PathFinder.HPA_STAR, new Tile(5,5), new Tile(5,30), null, null, 100);
     
        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest ended now!!!");

        w.debugPathOnMap(path);
        
        
    }

    @Test
    public void DifficultObstacleTest() {
        Logger.debug(LogCategory.JUNIT, "JUNIT ObstacleTestTest init");
        /*
         * generate map like this 
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
        Ants.INSTANCE.initClustering(8);

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        // Logger.debug(LogCategory.JUNIT, "JUNIT ObstacleTestTest hpastar");

        Ants.getClusters().getClusters()[0][0].debugEdges();
        Edge e = Ants.getClusters().getClusters()[0][0].edges.get(0);
        DirectedEdge edgeStart = new DirectedEdge(e.getTile1(), e.getTile2(), Ants.getClusters().getClusters()[0][0]);

        Ants.getClusters().getClusters()[0][Ants.getClusters().getCols() - 2].debugEdges();
        Edge e2 = Ants.getClusters().getClusters()[0][Ants.getClusters().getCols() - 2].edges.get(0);
        DirectedEdge edgeEnd = new DirectedEdge(e2.getTile1(), e2.getTile2(), Ants.getClusters().getClusters()[0][Ants.getClusters()
                .getCols() - 2]);

        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest start now!!!");
        long start = System.currentTimeMillis();
        List<Tile> path = PathFinder.bestPath(PathFinder.A_STAR, edgeStart, edgeEnd, null, null, 100);
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "HAPAstar unittest ended now!!! duration: %s milisecods", elapsed);

        w.debugPathOnMap(path);
        
        Logger.debug(LogCategory.JUNIT, "Astar unittest start now!!!");
        start = System.currentTimeMillis();
        path = PathFinder.bestPath(PathFinder.A_STAR, new Tile(7,8), new Tile(7,32), null, null, 100);
        elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "Astar unittest ended now!!! duration: %s milisecods", elapsed);

        w.debugPathOnMap(path);
        

    }
    
    
    

}
