package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import api.MapOutput;
import api.Tile;

public class SearchSpaceTest {

    @Test
    public void someWaterTest() {
        System.out.println("SomeWaterTest");
        String sMap = "";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";
        sMap += "oooooooooooooooo";

        UnitTestMap map = new UnitTestMap(16, sMap);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        Tile sp1 = new Tile(0, 0);
        Tile sp2 = new Tile(8, 16);

        // List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 14), new Tile(4, 1), -1); // , sp1, sp2,
        // -1);

        // List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 1), new Tile(4, 9), sp1, sp2, -1);

        List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 1), new Tile(4, 14), -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("SearchSpaceTest_SomeWaterTest");

        Assert.assertNotNull(path);
    }

}
