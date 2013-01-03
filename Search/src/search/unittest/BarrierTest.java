package search.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pathfinder.unittest.UnitTestMap;
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
        barrierCheck(hill, true);
    }

    @Test
    public void findBarrierTest2() {

        final Tile hill = new Tile(2, 34);
        barrierCheck(hill, true);
    }

    @Test
    public void findBarrierTest3() {

        final Tile hill = new Tile(8, 24);
        barrierCheck(hill, false);
    }

    private void barrierCheck(final Tile hill, boolean barrierShouldBeFound) {
        final UnitTestMap map = createMap();
        MapOutput put = new MapOutput();
        put.setMap(map);

        int viewRadiusSquared = 36;
        int maximumBarrierSize = 5;
        List<Tile> smallestBar = getBarrier(map, hill, viewRadiusSquared, maximumBarrierSize);

        if (smallestBar != null) {
            put.cleanUp(true, null);
            put.addComment("smallestBarrier found: " + smallestBar);
            put.addObject(smallestBar, "Barrier");
        }
        put.addObject(Arrays.asList(new Tile[] { hill }), "Hill");
        if (barrierShouldBeFound)
            Assert.assertNotNull(smallestBar);
        else
            Assert.assertNull(smallestBar);

        put.saveHtmlMap("BarrierTest_findBarrierTest_" + hill.getRow() + "_" + hill.getCol());
    }

    private List<Tile> getBarrier(UnitTestMap map, final Tile hill, int viewRadiusSquared, int maximumBarrierSize) {
        List<Tile> barrierVerticalInvalid = new ArrayList<Tile>();
        List<Tile> barrierHorizontalInvalid = new ArrayList<Tile>();

        List<Tile> smallestBar = null;
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> defaultFlood = bfs.findClosestTiles(hill, Integer.MAX_VALUE, Integer.MAX_VALUE, viewRadiusSquared,
                new AlwaysTrueGoalTest());

        int defaultFloodSize = defaultFlood.size();
        int additionalTiles = 40;
        // put.addObject(defaultFlood, "defaultFlood");
        List<Tile> exendedFlood = bfs.findClosestTiles(hill, defaultFloodSize + additionalTiles, Integer.MAX_VALUE,
                Integer.MAX_VALUE, new AlwaysTrueGoalTest());

        for (int i = 0; i < additionalTiles; i++) {
            // put.cleanUp(true, null);
            int tileCount = defaultFloodSize + i;
            Tile barrier = exendedFlood.get(tileCount);
            // put.addObject(Arrays.asList(new Tile[] { barrier }), "barrierTile");

            List<Tile> vert = new ArrayList<Tile>();
            if (!barrierVerticalInvalid.contains(barrier)) {
                vert.add(barrier);
                vert.addAll(map.getTilesToNextImpassableTile(barrier, Aim.SOUTH, maximumBarrierSize));
                vert.addAll(map.getTilesToNextImpassableTile(barrier, Aim.NORTH, maximumBarrierSize));
                // put.addObject(vert, "vert");
                if (vert.size() > maximumBarrierSize) {
                    barrierVerticalInvalid.addAll(vert);
                    // put.addComment("vertical is to long");
                    vert = new ArrayList<Tile>();
                }
            }
            List<Tile> hor = new ArrayList<Tile>();
            if (!barrierHorizontalInvalid.contains(barrier)) {
                hor.add(barrier);
                hor.addAll(map.getTilesToNextImpassableTile(barrier, Aim.EAST, maximumBarrierSize));
                hor.addAll(map.getTilesToNextImpassableTile(barrier, Aim.WEST, maximumBarrierSize));
                // put.addObject(hor, "hor");
                if (hor.size() > maximumBarrierSize) {
                    barrierHorizontalInvalid.addAll(hor);
                    // put.addComment("horizontal is to long");
                    hor = new ArrayList<Tile>();
                }
            }

            if (hor.size() == 0 && vert.size() == 0)
                continue;

            boolean useVertical = true;
            if (hor.size() > 0 && hor.size() < vert.size())
                useVertical = false;

            final List<Tile> bar = useVertical ? vert : hor;

            if (smallestBar != null && bar.size() >= smallestBar.size())
                continue;

            int fillUpVariance = 15;

            List<Tile> flood = bfs.findClosestTiles(hill, tileCount + fillUpVariance, Integer.MAX_VALUE,
                    Integer.MAX_VALUE, new AlwaysTrueGoalTest(), new FrontierTest() {
                        @Override
                        public boolean isFrontier(Tile tile) {
                            return !bar.contains(tile);
                        }

                    });

            // flood.removeAll(exendedFlood.subList(0, tileCount));
            // put.addObject(flood, "flood");
            // put.addObject(exendedFlood.subList(0, tileCount), "checkarea");

            if (flood.size() < tileCount + fillUpVariance) {
                smallestBar = bar;
                // put.addComment("currnent smallestBarrier is: " + smallestBar);
            }
            // put.addObject(bar, "Barrier");
            // put.addComment("Barrier size " + bar.size());
            // put.addComment("barrier didn't hold the flood: floodsize: " + flood.size() + " to hold " + tileCount);
        }
        return smallestBar;
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

}
