package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import api.entities.Tile;
import api.test.MapOutput;

public class SimpleSearchTest {

    @Test
    public void baseTest() {
        System.out.println("BaseTest");
        UnitTestMap map = new UnitTestMap(25, 25);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple, new Tile(10, 10), new Tile(15, 15), 20);

        Assert.assertNotNull(path);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "Simple Path");
        put.saveHtmlMap("SimpleSearchTest_baseTest");
    }

    @Test
    public void someWaterTest() {
        System.out.println("SomeWaterTest");
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooooooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple, new Tile(5, 2), new Tile(8, 17), -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "Simple Path");
        put.saveHtmlMap("SimpleSearchTest_someWaterTest");

        Assert.assertNotNull(path);
    }

    @Test
    public void globeTest() {
        System.out.println("GlobeTest");
        String sMap = "";
        sMap += "wowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowoooooooooo";
        sMap += "ooooooooooooowoooowooooooowoooooooooo";
        sMap += "oooooooooooooooooowooooooowoooooooooo";
        sMap += "wowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple, new Tile(8, 2), new Tile(6, 34), -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "Simple Path");
        put.saveHtmlMap("SimpleSearchTest_globeTest");

        Assert.assertNotNull(path);
    }
}
