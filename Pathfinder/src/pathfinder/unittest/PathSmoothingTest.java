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

public class PathSmoothingTest {

    @Test
    public void smoothThePathMenTest() {
        ClusterType type = ClusterType.Corner;
        String name = "PathSmoothingTest_smoothThePathMenTest_" + type;
        System.out.println(name);
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "wooooooooooowwooooooooooooooooooooow";
        sMap += "oooooooooooowwoooowwooooooooooooooow";
        sMap += "oooooooooooowwoooowwooooooooooooooow";
        sMap += "oooowwwooooowwoooowwwwwwwwooooooooow";
        sMap += "oooowwwooooowwoooowwwwwwwwooooooooow";
        sMap += "wooowwwooooooooooowwooooooooooooooow";
        sMap += "wooowwwoooooooooooowooooooooooooooow";
        sMap += "wooowwwoooooooooooowooooooooooooooow";
        sMap += "wooowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(36, sMap);

        int clusterSize = 9;
        ClusteringPathFinder pf = new ClusteringPathFinder(map, clusterSize, type);
        pf.update();

        Tile start = new Tile(7, 3);
        Tile end = new Tile(8, 20);

        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, start, end, -1);

        List<Tile> pathSmoothed = pf.smoothPath(path, 8, 3);
        // List<Tile> pathSmoothed = pf.smoothPath(pathSmoothed_, 12, 1);
        // pathSmoothed = pf.smoothPath(pathSmoothed, 12, 1);

        List<Tile> startEnd = new ArrayList<Tile>();
        startEnd.add(start);
        startEnd.add(end);

        MapOutput put = new MapOutput();
        put.setMap(pf.getMap());
        put.addObject(path, "HpaStar Path");
        put.setClusterSize(clusterSize);
        put.addObject(pf.getClustering().getAllVertices(), "Cluster Points");
        put.addObject(pathSmoothed, "Smoothed Path");
        put.addObject(startEnd, "Start and End Points");

        boolean vaildPath = pf.validatePath(path);
        System.out.println("smooth vaildated");
        boolean vaildSmoothedPath = pf.validatePath(pathSmoothed);
        put.addComment("Path: " + path.toString() + " Size: " + path.size() + " Is path valid ? " + vaildPath);
        put.addComment("Smoothed Path: " + pathSmoothed.toString() + " Size: " + pathSmoothed.size()
                + " Is path valid ? " + vaildSmoothedPath);

        put.saveHtmlMap(name);

        Assert.assertTrue(vaildPath);
        Assert.assertTrue(vaildSmoothedPath);

        Assert.assertTrue(path.size() >= pathSmoothed.size());

        Assert.assertNotNull(path);
        Assert.assertNotNull(pathSmoothed);
    }
}
