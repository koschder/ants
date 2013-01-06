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

/**
 * some tests to test the smoothing
 * 
 * @author kases1, kustl1
 * 
 */
public class PathSmoothingTest {

    @Test
    public void smoothThePathCornerTest() {
        ClusterType type = ClusterType.Corner;
        smoothThePathMenTest(type);
    }

    @Test
    public void smoothThePathCenteredTest() {
        ClusterType type = ClusterType.Centered;
        smoothThePathMenTest(type);
    }

    public void smoothThePathMenTest(ClusterType type) {
        String name = "PathSmoothingTest_smoothThePathMenTest_" + type;
        System.out.println(name);
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwooowwww";
        sMap += "woooooooowoowwooooooooooooooooooooow";
        sMap += "ooooooooowoowwooooooooooooooooooooow";
        sMap += "ooooooooowooooooooooowooooooooooooow";
        sMap += "oooowwwooooooooooowwwwwwwwooooooooow";
        sMap += "oooowwwooooooooooowwwwwwwwooooooooow";
        sMap += "wooowwwooooooooooowwooooooooooooooow";
        sMap += "wooowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(36, sMap);

        int clusterSize = 9;
        ClusteringPathFinder pf = new ClusteringPathFinder(map, clusterSize, type);
        pf.update();

        Tile start = new Tile(7, 3);
        Tile end = new Tile(3, 34);

        List<Tile> path = pf.search(PathFinder.Strategy.HpaStar, start, end, -1);

        List<Tile> pathSmoothed = pf.smoothPath(path, 8, true);
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
        put.addObject(startEnd, "Start and End Points");
        boolean vaildPath = pf.validatePath(path);
        put.addComment("Path: " + path.toString() + " Size: " + path.size() + " Is path valid ? " + vaildPath);

        put.cleanUp(true, null);
        put.addObject(pathSmoothed, "Smoothed Path");
        put.addObject(startEnd, "Start and End Points");

        // System.out.println("smooth vaildated");
        boolean vaildSmoothedPath = pf.validatePath(pathSmoothed);
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
