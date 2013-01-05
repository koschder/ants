package search.unittest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pathfinder.unittest.UnitTestMap;
import search.Barrier;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.AlwaysTrueGoalTest;
import search.BreadthFirstSearch.FrontierTest;
import api.entities.Aim;
import api.entities.Tile;
import api.test.MapOutput;

public class BarrierTest {

    @Test
    public void findBarrierTest1() {

        final Tile hill = new Tile(3, 6);
        barrierCheck(hill, true, Aim.EAST);
    }

    @Test
    public void findBarrierTest2() {

        final Tile hill = new Tile(2, 34);
        barrierCheck(hill, true, Aim.WEST);
    }

    @Test
    public void findBarrierTest3() {

        final Tile hill = new Tile(8, 24);
        barrierCheck(hill, false, null);
    }

    private void barrierCheck(final Tile hill, boolean barrierShouldBeFound, Aim direction) {
        final UnitTestMap map = createMap();
        MapOutput put = new MapOutput();
        put.setMap(map);

        int viewRadiusSquared = 36;
        int maximumBarrierSize = 5;

        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        Barrier bar = bfs.getBarrier(hill, viewRadiusSquared, maximumBarrierSize);

        List<Tile> smallestBar = null;
        if (bar != null) {
            smallestBar = bar.getBarrier();
            put.cleanUp(true, null);
            put.addComment("smallestBarrier found: " + smallestBar);
            put.addObject(smallestBar, "Barrier");
        }
        put.addObject(Arrays.asList(new Tile[] { hill }), "Hill");
        if (barrierShouldBeFound) {
            Assert.assertNotNull(smallestBar);
            Assert.assertEquals(direction, bar.getAimOfBarrier());
        } else
            Assert.assertNull(bar);

        put.saveHtmlMap("BarrierTest_findBarrierTest_" + hill.getRow() + "_" + hill.getCol());
    }

    @Test
    public void testBarrierTest() {

        MapOutput put = new MapOutput();
        final UnitTestMap map = createMap();
        put.setMap(map);

        final List<Tile> bar = Arrays.asList(new Tile[] { new Tile(1, 5), new Tile(2, 5), new Tile(3, 5),
                new Tile(4, 5) });
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> flood = bfs.findClosestTiles(new Tile(4, 7), 50, Integer.MAX_VALUE, Integer.MAX_VALUE,
                new AlwaysTrueGoalTest(), new FrontierTest() {
                    @Override
                    public boolean isFrontier(Tile tile) {
                        return !bar.contains(tile);
                    }

                });

        put.addObject(bar, "barrier");
        put.addObject(flood, "flood");

        put.saveHtmlMap("barrierHoldTest");

        Assert.assertEquals(23, flood.size());
    }

    @Test
    public void testBarrierTest2() {

        MapOutput put = new MapOutput();
        final UnitTestMap map = createMap2();
        put.setMap(map);

        int viewRadiusSquared = 36;
        int maximumBarrierSize = 5;
        Tile hill = new Tile(2, 2);
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        Barrier bar = bfs.getBarrier(hill, viewRadiusSquared, maximumBarrierSize);

        List<Tile> smallestBar = null;
        if (bar != null) {
            smallestBar = bar.getBarrier();
            put.cleanUp(true, null);
            put.addComment("smallestBarrier found: " + smallestBar);
            put.addObject(smallestBar, "Barrier");
            put.addObject(bar.getBarrierPlaceTiles(), "tiles place");
            put.addObject(bar.getBarrierPlaceTiles(), "tiles place");
            put.addObject(bar.getBarrierPlaceTiles(), "tiles place");
        }
        put.addObject(Arrays.asList(new Tile[] { hill }), "Hill");

        Assert.assertNotNull(smallestBar);
        Assert.assertEquals(Aim.EAST, bar.getAimOfBarrier());

        put.saveHtmlMap("BarrierTest_findBarrierTest_" + hill.getRow() + "_" + hill.getCol());
    }

    private UnitTestMap createMap() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooowwwwwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowwwooooooow";
        sMap += "woooooooooooowoooowooooooowwwooooooow";
        sMap += "woooooooooooooooxooooooooowwwooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        final UnitTestMap map = new UnitTestMap(37, sMap);
        return map;
    }

    private UnitTestMap createMap2() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "wooooooooooooooooooooooooowwwooooooow";
        sMap += "wooooooooooooooooooooooooowwwooooooow";
        sMap += "wooooooooooooooooooooooooowwwooooooow";
        sMap += "wooooooooooooooooooooooooowwwooooooow";
        sMap += "wooooooooooooooooooooooooowwwooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        final UnitTestMap map = new UnitTestMap(37, sMap);
        return map;
    }
}
