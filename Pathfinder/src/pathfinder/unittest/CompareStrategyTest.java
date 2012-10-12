package pathfinder.unittest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Tile;

public class CompareStrategyTest {

    @Test
    public void aStarTest() {
        String sTestName = "AStarTest";
        System.out.println(sTestName);
        PathFinder pf = initPathFinder();
        Tile start = new Tile(25, 70);
        Tile end = new Tile(45, 20);
        List<Tile> path = pf.search(PathFinder.Strategy.AStar, start, end, -1);
        if (path == null) {
            path = new ArrayList<Tile>();
            path.add(start);
            path.add(end);
        }

        ((UnitTestMap) pf.getMap()).saveHtmlMap(sTestName, path, -1);
        Assert.assertNotNull(path);

    }

    private PathFinder initPathFinder() {
        UnitTestMap map = initMap();
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        return pf;
    }

    private UnitTestMap initMap() {
        String sFileName = "maps/cell_maze_p04_10.map";
        try {
            UnitTestMap map = new UnitTestMap(parseFile(sFileName));
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static List<String> parseFile(String filePath) throws Exception {
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
