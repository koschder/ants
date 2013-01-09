package pathfinder.unittest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pathfinder.ClusteringPathFinder;
import pathfinder.PathFinder;
import pathfinder.PathFinder.Strategy;
import pathfinder.SimplePathFinder;
import pathfinder.entities.Clustering.ClusterType;
import api.entities.Tile;
import api.search.SearchableMap;
import api.test.MapOutput;

/**
 * these test compare the runtime of a star alogrithm
 * 
 * @author kases1, kustl1
 * 
 */
public class CompareStrategyTest {

    MapOutput putGlobal = new MapOutput();

    public enum SearchTest {
        Simple,
        AStar,
        HpaCent10,
        HpaCorn10,
        HpaCent20,
        HpaCorn20
    };

    @Test
    public void compareThem() {

        Tile start = new Tile(25, 74);
        Tile end = new Tile(85, 20);
        SearchableMap map = initMap();
        for (SearchTest s : SearchTest.values()) {

            MapOutput put = new MapOutput();

            searchPath(map, put, s, start, end);
            putGlobal.setMap(map);
            put.setMap(map);
            if (s == SearchTest.HpaCent10 || s == SearchTest.HpaCorn10)
                put.setClusterSize(10);
            if (s == SearchTest.HpaCent20 || s == SearchTest.HpaCorn20)
                put.setClusterSize(20);

            put.saveHtmlMap("CompareTest_" + s);
        }
        putGlobal.saveHtmlMap("All_Tested_Settings_Together");
    }

    private List<Tile> searchPath(SearchableMap map, MapOutput put, SearchTest s, Tile start, Tile end) {
        List<Tile> path = null;
        long duration = 0;
        if (s == SearchTest.AStar || s == SearchTest.Simple) {
            SimplePathFinder spf = new SimplePathFinder(map);
            long startTime = System.currentTimeMillis();
            path = spf.search(s == SearchTest.AStar ? Strategy.AStar : Strategy.Simple, start, end);
            duration = System.currentTimeMillis() - startTime;

        } else {

            int clusterSize = 14;
            if (s == SearchTest.HpaCent20 || s == SearchTest.HpaCorn20) {
                clusterSize = 20;
            }

            ClusterType ct = ClusterType.Centered;
            if (s == SearchTest.HpaCorn10 || s == SearchTest.HpaCorn20) {
                ct = ClusterType.Corner;
            }
            ClusteringPathFinder cpf = new ClusteringPathFinder(map, clusterSize, ct);
            long startTimeClustering = System.currentTimeMillis();
            cpf.update();
            put.addComment("Duration Clustering " + (System.currentTimeMillis() - startTimeClustering) + " ms.");
            put.addObject(cpf.getClustering().getAllVertices(), "Cluster Points");
            long startTime = System.currentTimeMillis();
            path = cpf.search(PathFinder.Strategy.HpaStar, start, end, -1);
            duration = System.currentTimeMillis() - startTime;
            long startTimeSmooth = System.currentTimeMillis();
            List<Tile> pathSmoothed = cpf.smoothPath(path, clusterSize + 5, true);
            put.addComment("Duration smoothPath " + (System.currentTimeMillis() - startTimeSmooth) + " ms.");
            put.addComment("smoothPath length: " + (pathSmoothed == null ? "null" : pathSmoothed.size()));
            put.addObject(pathSmoothed, "smoothPath for " + s);

        }
        put.addObject(Arrays.asList(start), "Start");
        put.addObject(Arrays.asList(end), "End");
        put.addComment("Duration: " + duration + " ms.");
        put.addComment("Path length: " + (path == null ? "null" : path.size()));
        put.addObject(path, "Path For " + s);
        putGlobal.addObject(path, "Path For  " + s + " found in " + duration + " ms. Size is: "
                + (path == null ? "null" : path.size()));
        return path;
    }

    private UnitTestMap initMap() {
        String sFileName = "../Search/maps/random_walk_10p_01.map";
        try {
            UnitTestMap map = new UnitTestMap(parseFile(sFileName));
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<String> parseFile(String filePath) throws Exception {
        List<String> rows = new ArrayList<String>();
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);

        String currentRecord;
        while ((currentRecord = br.readLine()) != null) {
            if (currentRecord.startsWith("m "))
                rows.add(currentRecord.substring(2));
        }

        br.close();

        return rows;
    }
}
