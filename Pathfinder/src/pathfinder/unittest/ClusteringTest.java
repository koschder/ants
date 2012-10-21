package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Cluster;
import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import api.entities.Tile;
import api.test.MapOutput;

public class ClusteringTest {
    @Test
    public void clusteringMapCornerTest() {
        clusteringTestByType(Clustering.ClusterType.Corner);
    }

    @Test
    public void clusteringMapCenteredTest() {
        clusteringTestByType(Clustering.ClusterType.Centered);
    }

    private void clusteringTestByType(Clustering.ClusterType clusterType) {
        System.out.println("ClusteringMapTest" + clusterType);
        String sMap = "";
        sMap += "woooowwwwwwwwwwwwwwwwwwwwww";
        int cols = sMap.length();
        sMap += "woooooooooowwwwwwwwwwooooow";
        sMap += "woooooooooowwwwwwwwwwooooow";
        sMap += "woooooooooowwwwwwwwwwooooow";
        sMap += "woooooooooowwwwwwwwwwooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooow";
        sMap += "woooooooooooowoooooooooooow";
        sMap += "wooooowwooooowoooowooooooow";
        sMap += "wooooowwoooooooooowooooooow";
        sMap += "wwwwwwwwwoowwwwwwwwooooowww";
        sMap += "woooooooooowwwwwwwowwooooow";
        sMap += "ooooooooooooooooooowwooooow";
        sMap += "ooooooooooooooooooowwoooooo";
        sMap += "ooooooooooowwwwwwwwwwoooooo";
        sMap += "oooooooowwwwwwwwwwwwwoooooo";
        sMap += "woooooooooooowoooooooooooow";
        sMap += "woooooooooooowoooowooooooow";
        sMap += "wooooooooooooooooowooooooow";
        sMap += "wooooooooooooooooowooooooow";
        sMap += "woooowwwwwwwwwwwwwooooowwww";

        UnitTestMap map = new UnitTestMap(cols, sMap);
        PathFinder pf = new PathFinder(map);
        int clusterSize = 9;
        pf.initClustering(clusterSize, clusterType);

        pf.cluster();

        Clustering clusterd = pf.getClustering();

        List<Tile> tiles = clusterd.getAllVertices();

        String name = "ClusterByType" + clusterType;

        MapOutput put = new MapOutput();
        put.setMap(map);
        put.setClusterSize(clusterSize);
        put.addAllUnits();
        put.addObject(tiles, "Clustering Point");
        put.saveHtmlMap(name);

    }

    @Test
    public void globeCornerTest() {
        globeTestbyType(ClusterType.Corner);
    }

    @Test
    public void globeCenteredTest() {
        globeTestbyType(ClusterType.Centered);
    }

    public void globeTestbyType(ClusterType type) {
        String name = "ClusteringTest_globeTest_" + type;
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
        PathFinder pf = new PathFinder(map);
        int clusterSize = 10;
        pf.initClustering(clusterSize, type);
        pf.cluster();

        pf.getClustering().printEdges();
        pf.getClustering().printVertices();

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

        Cluster c = pf.getClustering().getClusters()[0][3];
        // the last cluster should have 3 egdes and 3 vertices
        if (type == ClusterType.Centered) {
            Assert.assertEquals(1, c.getEdges().size());
            Assert.assertEquals(2, c.getVertices().size());
        } else {
            Assert.assertEquals(3, c.getEdges().size());
            Assert.assertEquals(3, c.getVertices().size());
        }
    }

    @Test
    public void oneClusterTestCorner() {
        oneClusterTest(ClusterType.Corner);

    }

    @Test
    public void oneClusterTestCentered() {
        oneClusterTest(ClusterType.Centered);
    }

    public void oneClusterTest(ClusterType type) {
        String name = "ClusteringTest_oneClusterTest_" + type;
        System.out.println(name);
        String sMap = "";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oowwoooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";
        sMap += "oooooooooo";

        UnitTestMap map = new UnitTestMap(10, sMap);
        PathFinder pf = new PathFinder(map);
        int clusterSize = 5;
        pf.initClustering(clusterSize, type);
        pf.cluster();

        pf.getClustering().printEdges();
        pf.getClustering().printVertices();

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

        for (int i = 0; i < pf.getClustering().getClusters().length; i++) {
            for (int j = 0; j < pf.getClustering().getClusters()[0].length; j++) {
                Cluster c = pf.getClustering().getClusters()[i][j];
                Assert.assertEquals(6, c.getEdges().size());
                Assert.assertEquals(4, c.getVertices().size());

            }

        }

    }

    @Test
    public void beeLineTest() {

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

        double line = map.beelineTo(new Tile(4, 34), new Tile(7, 0));
        double man = map.manhattanDistance(new Tile(4, 34), new Tile(7, 0));
        boolean beeIsShorter = line < man;
        Assert.assertTrue("beeline must be shorter", beeIsShorter);
        Assert.assertTrue("beeline must be under 15", line < 15);

    }

}
