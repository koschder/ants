package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import api.entities.Tile;
import api.test.MapOutput;

import static org.junit.Assert.*;

/**
 * doing some tests to prove functionality of Astar
 * 
 * @author kases1, kustl1
 * 
 */
public class FindAStarTest {

    @Test
    public void baseTest() {
        System.out.println("BaseTest");
        UnitTestMap map = new UnitTestMap(25, 25);
        SimplePathFinder pf = new SimplePathFinder(map);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, new Tile(10, 10), new Tile(15, 15), 20);
        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("FindAStarTest_BaseTest");
        Assert.assertNotNull(path);

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
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        SimplePathFinder pf = new SimplePathFinder(map);
        final Tile start = new Tile(2, 2);
        final Tile end = new Tile(2, 35);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, start, end, -1);
        assertEquals(start, path.get(0));
        assertEquals(end, path.get(path.size() - 1));
        assertTrue(pf.validatePath(path));
        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Path");
        put.saveHtmlMap("FindAStarTest_SomeWaterTest");

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
        sMap += "wooooooooooooooooowooooooowooooooooow";
        sMap += "wowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        SimplePathFinder pf = new SimplePathFinder(map);
        final Tile start = new Tile(2, 2);
        final Tile end = new Tile(2, 35);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, start, end, -1);
        assertEquals(start, path.get(0));
        assertEquals(end, path.get(path.size() - 1));
        assertTrue(pf.validatePath(path));

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "A Star Pat");
        put.saveHtmlMap("FindAStarTest_GlobeTest");

        Assert.assertNotNull(path);
    }
}
