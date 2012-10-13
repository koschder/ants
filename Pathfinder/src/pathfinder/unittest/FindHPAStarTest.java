package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Clustering.ClusterType;
import api.MapOutput;
import api.Tile;

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
        PathFinder pf = new PathFinder();
        int clusterSize = 8;
        pf.setMap(map);
        pf.initClustering(clusterSize, type);
        pf.cluster();

        // pf.getClustering().printEdges();

        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, new Tile(10, 10), new Tile(15, 15), 20);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

        Assert.assertNotNull(path);
    }

    @Test
    public void someWaterTest() {
        someWaterTestbyType(ClusterType.Corner);
        // someWaterTestbyType(ClusterType.Centered);
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
        PathFinder pf = new PathFinder();
        int clusterSize = 8;
        pf.setMap(map);
        pf.initClustering(clusterSize, type);
        pf.cluster();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, new Tile(2, 2), new Tile(2, 35), -1);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

        Assert.assertNotNull(path);
    }

    @Test
    public void globeTest() {
        globeTestbyType(ClusterType.Corner);
        // globeTestbyType(ClusterType.Centered);
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
        PathFinder pf = new PathFinder();
        int clusterSize = 10;
        pf.setMap(map);
        pf.initClustering(clusterSize, type);
        pf.cluster();
        pf.setMap(map);
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
