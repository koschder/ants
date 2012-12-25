package search.unittest;

import java.util.List;

import org.junit.Test;

import pathfinder.unittest.UnitTestMap;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import api.entities.Tile;

import static org.junit.Assert.*;

/**
 * some tests for the bfs
 * 
 * @author kaeserst, kustl1
 * 
 */
public class BreadthFirstSearchTest {
    @Test
    public void testFindSingleClosestTile() {
        final UnitTestMap map = createMap();
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        Tile closestTile = bfs.findSingleClosestTile(new Tile(6, 15), 100, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return map.isFriendlyUnit(tile);
            }
        });
        assertEquals(new Tile(8, 16), closestTile);
    }

    private UnitTestMap createMap() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "woooooooooooooooxooooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        final UnitTestMap map = new UnitTestMap(37, sMap);
        return map;
    }

    @Test
    public void testFindClosestTiles_TooManyExplored() {
        final UnitTestMap map = createMap();
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> tiles = bfs.findClosestTiles(new Tile(6, 15), Integer.MAX_VALUE, 10, Integer.MAX_VALUE,
                new GoalTest() {

                    @Override
                    public boolean isGoal(Tile tile) {
                        return false;
                    }
                });
        assertTrue(tiles.isEmpty());
    }

    @Test
    public void testFindClosestTiles_reachedMaxNumberOfHits() {
        final UnitTestMap map = createMap();
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> tiles = bfs.findClosestTiles(new Tile(6, 15), 5, Integer.MAX_VALUE, Integer.MAX_VALUE,
                new GoalTest() {

                    @Override
                    public boolean isGoal(Tile tile) {
                        return true;
                    }
                });
        assertEquals(5, tiles.size());
    }

    @Test
    public void testFindClosestTiles_distanceTooLarge() {
        final UnitTestMap map = createMap();
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> tiles = bfs.findClosestTiles(new Tile(7, 16), Integer.MAX_VALUE, Integer.MAX_VALUE, 1,
                new GoalTest() {

                    @Override
                    public boolean isGoal(Tile tile) {
                        return true;
                    }
                });
        assertEquals(4, tiles.size());
    }
}
