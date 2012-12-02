package unittest.ants.search;

import java.util.List;

import org.junit.Test;

import pathfinder.unittest.UnitTestMap;
import ants.search.BreadthFirstSearch;
import ants.search.BreadthFirstSearch.GoalTest;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

import static org.junit.Assert.*;

public class BreadthFirstSearchTest {
    @Test
    public void testFindClosestTile() {
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
        BreadthFirstSearch bfs = new BreadthFirstSearch() {
            @Override
            protected List<SearchTarget> getSuccessors(Tile next) {
                return map.getSuccessors(next, false);
            }
        };
        Tile closestTile = bfs.findSingleClosestTile(new Tile(6, 15), 100, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return map.isFriendlyUnit(tile);
            }
        });
        assertEquals(new Tile(8, 16), closestTile);
    }
}
