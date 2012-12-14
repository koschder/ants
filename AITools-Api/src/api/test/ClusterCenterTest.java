package api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import api.entities.Tile;
import api.map.AbstractWraparoundMap;
import api.pathfinder.SearchTarget;
import api.strategy.InfluenceMap;

import static org.junit.Assert.*;

public class ClusterCenterTest {
    @Test
    public void testFindClusterCenter() {
        ClusterFindMap map = new ClusterFindMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(10, 10));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(15, 15), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_3Points() {
        ClusterFindMap map = new ClusterFindMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(0, 0));
        cluster.add(new Tile(0, 30));
        cluster.add(new Tile(30, 30));

        assertEquals(new Tile(10, 20), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_2Points_wraparound() {
        ClusterFindMap map = new ClusterFindMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(90, 90));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(5, 5), map.getClusterCenter(cluster));
    }

    @Test
    public void testFindClusterCenter_3Points_wraparound() {
        ClusterFindMap map = new ClusterFindMap(100, 100);
        List<Tile> cluster = new ArrayList<Tile>();
        cluster.add(new Tile(90, 90));
        cluster.add(new Tile(90, 20));
        cluster.add(new Tile(20, 20));

        assertEquals(new Tile(0, 10), map.getClusterCenter(cluster));
    }

    static class ClusterFindMap extends AbstractWraparoundMap {

        public ClusterFindMap(int rows, int cols) {
            super(rows, cols);
        }

        @Override
        public List<SearchTarget> getSuccessorsForPathfinding(SearchTarget currentPosition, boolean isNextMove) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SearchTarget> getSuccessorsForSearch(SearchTarget target, boolean isNextMove) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Tile getSafestNeighbour(Tile tile, InfluenceMap influenceMap) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getRows() {
            return rows;
        }

        @Override
        public int getCols() {
            return cols;
        }

        @Override
        public boolean isPassable(Tile tile) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isVisible(Tile tile) {
            // TODO Auto-generated method stub
            return false;
        }

    }
}
