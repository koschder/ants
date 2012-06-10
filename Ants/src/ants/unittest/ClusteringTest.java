package ants.unittest;

import junit.framework.Assert;

import org.junit.Test;

import ants.entities.Tile;
import ants.search.Clustering;
import ants.state.Ants;
import ants.state.World;
import ants.tasks.ClusteringTask;

public class ClusteringTest {

    @Test
    public void test(){
        
        Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
        Clustering c = new Clustering(7);
        
        Assert.assertEquals(6, c.getRows());
        Assert.assertEquals(6, c.getCols());
        
        
        Clustering c2 = new Clustering(8);
        
        Assert.assertEquals(5, c2.getRows());
        Assert.assertEquals(5, c2.getCols());
        
        
        
        
    }
    
    @Test
    public void testEverythingClustered(){
    World w = new World(40, 40, 5, 5, 5);
    w.setEverythingVisibleAndPassable();
//    w.setWater(new Tile(0, 38), new Tile(40, 40));
//    w.setWater(new Tile(38, 0), new Tile(40, 40));
//    w.setWater(new Tile(0, 0), new Tile(2, 40));
//    w.setWater(new Tile(0, 0), new Tile(40, 2));
//    w.setWater(new Tile(0, 10), new Tile(20, 20));
    Ants.INSTANCE.setup(0, 0, 40, 40, 0, 20, 20, 10);
    Ants.INSTANCE.setWorld(w);
    Ants.INSTANCE.initClustering(7);

    ClusteringTask task = new ClusteringTask();
    task.setup();
    task.perform();
    int amount = Ants.INSTANCE.getClusters().getRows()  * Ants.INSTANCE.getClusters().getCols(); 
    for(int i = 0;i < amount;i++ ){
        System.out.println("Idx: "+i+" is clustered "+Ants.INSTANCE.getClusters().getCluster(i).isClustered());
        Assert.assertTrue(Ants.INSTANCE.getClusters().getCluster(i).isClustered());
    }
    
    
    
    }
    
    
    @Test
    public void testEverythingWithWaterClustered(){
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
    int amount = Ants.INSTANCE.getClusters().getRows()  * Ants.INSTANCE.getClusters().getCols(); 
    for(int i = 0;i < amount;i++ ){
        System.out.println("Idx: "+i+" is clustered "+Ants.INSTANCE.getClusters().getCluster(i).isClustered());
        Assert.assertTrue(Ants.INSTANCE.getClusters().getCluster(i).isClustered());
    }
    
    
    
    }
 
}
