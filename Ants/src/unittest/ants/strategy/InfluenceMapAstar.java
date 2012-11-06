package unittest.ants.strategy;

import influence.DefaultInfluenceMap;
import influence.unittest.UnitTestInfluenceMap;

import java.util.List;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import api.entities.Tile;
import api.strategy.InfluenceMap;
import api.test.MapOutput;
import api.test.PixelDecorator;

public class InfluenceMapAstar {

    @Test
    public void aStarInfluenceTest() {

        System.out.println("aStarInfluenceTest");
        String sMap = getDefaultTestMap();

        UnitTestInfluenceMap map = new UnitTestInfluenceMap(37, sMap);

        final Tile start = new Tile(2, 2);
        final Tile end = new Tile(2, 35);
        SimplePathFinder pf = new SimplePathFinder(map);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, start, end, -1);

        final InfluenceMap iMap = new DefaultInfluenceMap(map, 8, 4);
        SimplePathFinder pfInf = new SimplePathFinder(map, iMap);
        List<Tile> pathInf = pfInf.search(PathFinder.Strategy.AStar, start, end, -1);

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.addObject(path, "Path without Influence");
        put.addObject(pathInf, "Path with Influence");
        put.addComment("Path costs of path 1 without influence map:" + pf.getPathCosts(path));
        put.addComment("Path costs of path 1 with influence map:" + pfInf.getPathCosts(path) + " ["
                + pfInf.getPathCostsString(path) + "]");
        put.addComment("Path costs of path 2 without influence map:" + pf.getPathCosts(pathInf));
        put.addComment("Path costs of path 2 with influence map:" + pfInf.getPathCosts(pathInf) + " ["
                + pfInf.getPathCostsString(pathInf) + "]");
        put.setClusterSize(5);
        put.addAllUnits();
        put.saveHtmlMap("InfluenceMapAstar_aStarInfluenceTest", getSafetyDecorator(iMap));

    }

    private String getDefaultTestMap() {
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woo0o1oooo2ooo2ooo2o2oo0ooooooo0oooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooo0ooowwwwwwwwwwwwwooooowooo1ooooow";
        sMap += "woooooooooooow0ooooooo0ooowooooooooow";
        sMap += "wooooooooooo0woo0owoooooo2wooooooooow";
        sMap += "woooo1oooooooooooowooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        return sMap;
    }

    private PixelDecorator getSafetyDecorator(final InfluenceMap iMap) {
        return new PixelDecorator() {

            @Override
            public String getLabel(Tile tile) {
                return String.valueOf(iMap.getSafety(tile));
            }
        };
    }

}
