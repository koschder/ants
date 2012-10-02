package pathfinder.unittest;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pathfinder.PathFinder;
import pathfinder.entities.Tile;

public class SimpleSearchTest {

    @Test
    public void BaseTest() {
        System.out.println("BaseTest");
        UnitTestMap map = new UnitTestMap(25, 25);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple, new Tile(10, 10), new Tile(15, 15), 20);
        map.printMap(path);
        Assert.assertNotNull(path);

        map.printMap(path);
        String row = "";
        for (Tile t : path) {
            row += t.getRow() + ":" + t.getCol() + "=>";
        }
        System.out.println(row);
    }

    @Test
    public void SomeWaterTest() {
        System.out.println("SomeWaterTest");
        String sMap = "";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowooooooooow";
        sMap += "woooooooooooowoooowooooooowooooooooow";
        sMap += "wooooooooooooooooooooooooowooooooooow";
        sMap += "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple,new Tile(5, 2), new Tile(8, 17), -1);
        
        map.printMap(path);
        String row = "";
        for (Tile t : path) {
            row += t.getRow() + ":" + t.getCol() + "=>";
        }
        System.out.println(row);
        
        Assert.assertNotNull(path);
    }
    
    @Test
    public void GlobeTest() {
        System.out.println("GlobeTest");
        String sMap = "";
        sMap += "wowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooooooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooowwwwwwwwwwooooowooooooooow";
        sMap += "wooooooowwwwwwwwwwwwwooooowooooooooow";
        sMap += "woooooooooooowoooooooooooowoooooooooo";
        sMap += "ooooooooooooowoooowooooooowoooooooooo";
        sMap += "oooooooooooooooooowooooooowoooooooooo";
        sMap += "wowwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";

        UnitTestMap map = new UnitTestMap(37, sMap);
        PathFinder pf = new PathFinder();
        pf.setMap(map);
        List<Tile> path = pf.search(PathFinder.Strategy.Simple,new Tile(8, 2), new Tile(6, 34), -1);
        
        map.printMap(path);
        String row = "";
        for (Tile t : path) {
            row += t.getRow() + ":" + t.getCol() + "=>";
        }
        System.out.println(row);
        
        Assert.assertNotNull(path);
    }
}
