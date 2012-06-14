package ants.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ants.entities.Edge;
import ants.entities.Tile;
import ants.search.Clustering;
import ants.state.Ants;
import ants.state.World;
import ants.tasks.ClusteringTask;

public class ClusteringTest {

    /***
     * Testing the size of a cluster.
     */
    @Test
    public void clusterSizeTest() {

        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Clustering c = new Clustering(7);

        Assert.assertEquals(6, c.getRows());
        Assert.assertEquals(6, c.getCols());

        Clustering c2 = new Clustering(8);

        Assert.assertEquals(5, c2.getRows());
        Assert.assertEquals(5, c2.getCols());

    }
/***
 * check clustering of a map without water.
 */
    @Test
    public void testEverythingClustered() {
        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(7);

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        int amount = Ants.getClusters().getRows() * Ants.getClusters().getCols();
        for (int i = 0; i < amount; i++) {
            System.out
                    .println("Idx: " + i + " is clustered " + Ants.getClusters().getCluster(i).isClustered());
            Assert.assertTrue(Ants.getClusters().getCluster(i).isClustered());
        }
    }
/***
 * checks if every edge in the cluster has its path calculated.
 */
    @Test
    public void testEveryEdgeAPathClustered() {
        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(7);

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        
        int amount = Ants.getClusters().getRows() * Ants.getClusters().getCols();
        for (int i = 0; i < amount; i++) {
          List<Edge> edges =   Ants.getClusters().getCluster(i).getEdges();
          for(Edge e : edges){
              if(!e.hasPath())
                  System.out.println("Edge has no path:"+e);
              Assert.assertTrue(e.hasPath());
          }
        }
    }
/***
 * checks if clustering works even with water.
 */
    @Test
    public void testEverythingWithWaterClustered() {
        World w = new World(40, 40, 5, 5, 5);
        w.setEverythingVisibleAndPassable();
        w.setWater(new Tile(0, 38), new Tile(40, 40));
        w.setWater(new Tile(38, 0), new Tile(40, 40));
        w.setWater(new Tile(0, 0), new Tile(2, 40));
        w.setWater(new Tile(0, 0), new Tile(40, 2));
        w.setWater(new Tile(0, 10), new Tile(20, 20));
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(8);

        ClusteringTask task = new ClusteringTask();
        task.setup();
        task.perform();
        int amount = Ants.getClusters().getRows() * Ants.getClusters().getCols();
        for (int i = 0; i < amount; i++) {
            System.out
                    .println("Idx: " + i + " is clustered " + Ants.getClusters().getCluster(i).isClustered());
            Assert.assertTrue(Ants.getClusters().getCluster(i).isClustered());
        }
    }
}
