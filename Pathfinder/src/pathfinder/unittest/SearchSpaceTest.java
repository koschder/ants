package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import api.entities.Tile;
import api.test.MapOutput;

public class SearchSpaceTest {

    @Test
    public void someOverBorderSearchSpace1() {
        System.out.println("someOverBorderSearchSpace1");
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
        SimplePathFinder pf = new SimplePathFinder(map);
        Tile sp1 = new Tile(4, 4);
        Tile sp2 = new Tile(80, 80);

        List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 4), new Tile(2, 2), sp1, sp2, -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("SearchSpaceTest_someOverBorderSearchSpace1");

        Assert.assertNotNull(path);
    }

    @Test
    public void someOverBorderSearchSpace2() {
        System.out.println("someOverBorderSearchSpace2");
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
        SimplePathFinder pf = new SimplePathFinder(map);
        Tile sp1 = new Tile(0, 10);
        Tile sp2 = new Tile(8, 17);

        List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 12), new Tile(4, 2), sp1, sp2, -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("SearchSpaceTest_someOverBorderSearchSpace2");

        Assert.assertNull(path);
    }

    @Test
    public void someOverBorderSearchSpace3() {
        System.out.println("someOverBorderSearchSpace3");
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
        SimplePathFinder pf = new SimplePathFinder(map);
        Tile sp1 = new Tile(0, 10);
        Tile sp2 = new Tile(8, 20);

        List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(4, 12), new Tile(4, 2), sp1, sp2, -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("SearchSpaceTest_someOverBorderSearchSpace3");

        Assert.assertNotNull(path);
    }

}
