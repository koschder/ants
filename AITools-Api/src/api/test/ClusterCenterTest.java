package api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import api.entities.Tile;

import static org.junit.Assert.*;

/**
 * test for calculating the cluster center
 * 
 * @author kaeserst, kustl1
 * 
 */
public class ClusterCenterTest {
    @Test
    public void testFindClusterCenter() {
        SimpleUnitTestMap map = new SimpleUnitTestMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(10, 10));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(15, 15), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_3Points() {
        SimpleUnitTestMap map = new SimpleUnitTestMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(0, 0));
        cluster.add(new Tile(0, 30));
        cluster.add(new Tile(30, 30));

        assertEquals(new Tile(10, 20), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_3Points2() {
        SimpleUnitTestMap map = new SimpleUnitTestMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(10, 10));
        cluster.add(new Tile(11, 10));
        cluster.add(new Tile(11, 11));

        assertEquals(new Tile(11, 10), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_2Points_wraparound() {
        SimpleUnitTestMap map = new SimpleUnitTestMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(90, 90));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(5, 5), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_3Points_wraparound() {
        SimpleUnitTestMap map = new SimpleUnitTestMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(90, 90));
        cluster.add(new Tile(90, 20));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(0, 10), map.getClusterCenter(cluster));
    }
}
