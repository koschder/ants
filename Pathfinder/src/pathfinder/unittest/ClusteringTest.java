package pathfinder.unittest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Clustering;
import pathfinder.entities.Tile;
import pathfinder.entities.Vertex;

public class ClusteringTest {
    @Test
    public void ClusteringMapCornerTest() {
        clusteringTestByType(Clustering.ClusterType.Corner);
    }

    @Test
    public void ClusteringMapCenteredTest() {
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
        int clusterSize = 8;
        pf.setMap(map);
        pf.InitClustering(clusterSize, clusterType);

        pf.cluster();

        Clustering clusterd = pf.getClustering();

        List<Vertex> verts = clusterd.getAllVertices();

        List<Tile> tiles = new ArrayList<Tile>();

        for (Vertex x : verts) {
            tiles.add(x.getTargetTile());
        }
        String name = "ClusterByType" + clusterType;
        map.printMap(tiles, clusterSize);
        map.saveHtmlMap(name, tiles, clusterSize);
    }
}
