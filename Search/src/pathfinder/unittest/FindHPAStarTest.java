package pathfinder.unittest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.ClusteringPathFinder;
import pathfinder.PathFinder;
import pathfinder.entities.Clustering.ClusterType;
import api.entities.Tile;
import api.test.MapOutput;

import static org.junit.Assert.*;

/**
 * doing some tests to prove functionality of HPA*
 * 
 * @author kases1, kustl1
 * 
 */
public class FindHPAStarTest {

    @Test
    public void baseTest() {
        baseTestbyType(ClusterType.Corner);
        baseTestbyType(ClusterType.Centered);
    }

    private void baseTestbyType(ClusterType type) {
        String name = "FindHPAStarTest_BaseTest_" + type;
        System.out.println();
        UnitTestMap map = new UnitTestMap(25, 25);
        int clusterSize = 8;
        ClusteringPathFinder pf = new ClusteringPathFinder(map, clusterSize, type);
        pf.update();

        Tile start = new Tile(10, 10);
        Tile end = new Tile(15, 20);
        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, start, end, -1);

        List<Tile> startEnd = new ArrayList<Tile>();
        startEnd.add(start);
        startEnd.add(end);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.addComment("Start: " + start + " end: " + end);
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.addObject(startEnd, "Start and End Points");
        put.saveHtmlMap(name);
        assertEquals(start, path.get(0));
        assertEquals(end, path.get(path.size() - 1));
        Assert.assertNotNull(path);
    }

    @Test
    public void someWaterTest() {
        someWaterTestbyType(ClusterType.Corner);
    }

    public void someWaterTestbyType(ClusterType type) {
        String name = "FindHPAStarTest_SomeWaterTest_" + type;
        System.out.println(name);
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
        int clusterSize = 8;
        ClusteringPathFinder pf = new ClusteringPathFinder(map, clusterSize, type);
        pf.update();
        final Tile start = new Tile(2, 2);
        final Tile end = new Tile(2, 35);
        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, start, end, -1);

        List<Tile> startEnd = new ArrayList<Tile>();
        startEnd.add(start);
        startEnd.add(end);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.addComment("Path " + path.toString());
        put.addObject(startEnd, "Start and End Points");

        put.saveHtmlMap(name);

        assertEquals(start, path.get(0));
        assertEquals(end, path.get(path.size() - 1));

        Assert.assertNotNull(path);
    }

    @Test
    public void globeTest() {
        globeTestbyType(ClusterType.Corner);
        globeTestbyType(ClusterType.Centered);
    }

    public void globeTestbyType(ClusterType type) {
        String name = "FindHPAStarTest_globeTest_" + type;
        System.out.println(name);
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
        int clusterSize = 10;
        ClusteringPathFinder pf = new ClusteringPathFinder(map, clusterSize, type);
        pf.update();
        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, new Tile(2, 2), new Tile(2, 35), -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

        Assert.assertNotNull(path);
    }
}
