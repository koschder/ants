package ants.unittest;

import java.util.List;

import org.junit.Test;

import ants.entities.Tile;
import ants.search.PathFinder;
import ants.state.Ants;
import ants.state.World;
import ants.tasks.ClusteringTask;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class HpaStarTest {


    @Test
    public void obstacleTest() {
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

    /***
     * comparing astar with hpastar
     */
    @Test
    public void compareAtoAstarText() {
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
    
    
    /***
     * thest hpa* with difficulte obstacles
     */
    @Test
    public void difficultObstacleTest() {
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
