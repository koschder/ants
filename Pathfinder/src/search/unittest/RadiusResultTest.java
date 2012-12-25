package search.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pathfinder.unittest.UnitTestMap;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import api.entities.Tile;
import api.test.MapOutput;

/**
 * this tests are prove the functionalities of bfs to get tiles on a radius, used for formation our units
 * 
 * @author kaeserst, kustl1
 * 
 */
public class RadiusResultTest {
    @Test
    public void testFindSingleClosestTile1() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(15, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 5);
    }

    @Test
    public void testFindSingleClosestTile2() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(10, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 6);
    }

    @Test
    public void testFindSingleClosestTile3() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(9, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 15);
    }

    @Test
    public void testFindSingleClosestTile4() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(6, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 3);
    }

    @Test
    public void testFindSingleClosestTile5() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(7, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 2);
    }

    @Test
    public void testFindSingleClosestTile6() {
        Tile clusterCenter = new Tile(5, 10);
        final Tile enemyClusterCenter = new Tile(8, 10);
        getFormationTiles(clusterCenter, enemyClusterCenter, 11);
    }

    @Test
    public void testFindSingleClosestTile7() {
        Tile clusterCenter = new Tile(6, 10);
        final Tile enemyClusterCenter = new Tile(8, 16);
        getFormationTiles(clusterCenter, enemyClusterCenter, 4);
    }

    private void getFormationTiles(Tile clusterCenter, final Tile enemyClusterCenter, int ants) {
        final UnitTestMap map = createMap();
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        int squaredDistance = map.getSquaredDistance(clusterCenter, enemyClusterCenter);
        int distance = (int) Math.sqrt(squaredDistance);
        if (true) {
            distance--;
        }

        ants = ants % 2 == 0 ? ants + 1 : ants; // proof symmetric formation tiles

        List<Tile> formationTiles = new ArrayList<Tile>();
        List<Tile> tempTiles;
        do {
            distance++;
            final int dist = (int) Math.pow(distance, 2);
            final int minDist = dist - distance * 2;
            final int maxDist = dist;
            final int maxSpread = (int) Math.pow(distance * 2 - 1.5, 2);
            tempTiles = bfs.findClosestTiles(clusterCenter, ants - formationTiles.size(), Integer.MAX_VALUE, maxSpread,
                    new GoalTest() {

                        @Override
                        public boolean isGoal(Tile tile) {
                            final int distance = map.getSquaredDistance(tile, enemyClusterCenter);
                            return distance >= minDist && distance <= maxDist;
                        }
                    });
            formationTiles.addAll(tempTiles);
        } while (tempTiles.size() > 0 && formationTiles.size() < ants);

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.addObject(formationTiles, "Radius");
        put.addObject(Arrays.asList(new Tile[] { clusterCenter }), "AttackCenter");
        put.addObject(Arrays.asList(new Tile[] { enemyClusterCenter }), "EnemyCenter");
        put.addComment(formationTiles.size() + " Tiles found for " + ants + " Ants");
        put.saveHtmlMap("RadiusResultTest_testFindSingleClosestTile_" + squaredDistance);

    }

    private UnitTestMap createMap() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wooooooooooooooooooooooooooooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        final UnitTestMap map = new UnitTestMap(37, sMap);
        return map;
    }
}
