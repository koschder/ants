package ants.unittest;

import java.util.List;

import org.junit.Test;

import ants.entities.Tile;
import ants.search.PathFinder;
import ants.state.Ants;
import ants.state.World;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class AstarTest {

    
    @Test
    public void ObstacleTest() {
        Logger.debug(LogCategory.JUNIT, "JUNIT ObstacleTestTest init");
        initClusteredMap();
        
        Logger.debug(LogCategory.JUNIT, "A-star unittest start now!!!");
        long start = System.currentTimeMillis();
        List<Tile>path = PathFinder.bestPath(PathFinder.A_STAR, new Tile(3,3), new Tile(3,25),100);
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.JUNIT, "A-star  unittest ended now!!! duration: %s milisecods", elapsed);
        
        Ants.getWorld().debugPathOnMap(path);
        
        
    }

    private void initClusteredMap() {
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
        w.setWater(new Tile(0, 38), new Tile(40, 40));
        w.setWater(new Tile(38, 0), new Tile(40, 40));
        w.setWater(new Tile(0, 0), new Tile(2, 40));
        w.setWater(new Tile(0, 0), new Tile(40, 2));
        w.setWater(new Tile(0, 10), new Tile(20, 20));
        w.setWater(new Tile(5, 5), new Tile(10, 30));
        
        
        Ants.INSTANCE.setup(0, 0, 30, 10, 0, 20, 20, 10);
        Ants.INSTANCE.setWorld(w);
        Ants.INSTANCE.initClustering(10);

    }
    
    
}
