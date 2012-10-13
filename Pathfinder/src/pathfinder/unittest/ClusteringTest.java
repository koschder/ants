package pathfinder.unittest;

import java.util.List;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Clustering;
import pathfinder.entities.Clustering.ClusterType;
import api.MapOutput;
import api.Tile;

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
        PathFinder pf = new PathFinder();
        int clusterSize = 9;
        pf.setMap(map);
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
    public void globeTest() {
        globeTestbyType(ClusterType.Corner);
        // globeTestbyType(ClusterType.Centered);
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
        PathFinder pf = new PathFinder();
        int clusterSize = 10;
        pf.setMap(map);
        pf.initClustering(clusterSize, type);
        pf.cluster();
        pf.setMap(map);

        pf.getClustering().printEdges();
        pf.getClustering().printVertices();

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.saveHtmlMap(name);

    }
}
