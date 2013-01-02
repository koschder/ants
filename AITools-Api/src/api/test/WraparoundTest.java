package api.test;

import org.junit.Test;

import api.entities.Aim;
import api.entities.Tile;
import api.search.SearchableMap;

import static org.junit.Assert.*;

/**
 * theses methods prove correct functionality of the wraparaound map
 * 
 * @author kases1, kustl1
 * 
 */
public class WraparoundTest {

    /**
     * Test getPrincipalDirection() with the same tile
     */
    @Test
    public void testGetPrincipalDirection_directionToSelf() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile center = new Tile(5, 5);
        assertNull(map.getPrincipalDirection(center, center));
    }

    /**
     * Test getPrincipalDirection() with adjacent tiles
     */
    @Test
    public void testGetPrincipalDirection_simple1() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile center = new Tile(5, 5);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(center, new Tile(4, 5)));
        assertEquals(Aim.WEST, map.getPrincipalDirection(center, new Tile(5, 4)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(center, new Tile(6, 5)));
        assertEquals(Aim.EAST, map.getPrincipalDirection(center, new Tile(5, 6)));
    }

    /**
     * Test getPrincipalDirection() with tiles that are more than 1 step away, but that have only 1 direction component
     */
    @Test
    public void testGetPrincipalDirection_simple2() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile center = new Tile(5, 5);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(center, new Tile(3, 5)));
        assertEquals(Aim.WEST, map.getPrincipalDirection(center, new Tile(5, 3)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(center, new Tile(7, 5)));
        assertEquals(Aim.EAST, map.getPrincipalDirection(center, new Tile(5, 7)));
    }

    /**
     * Test getPrincipalDirection() with adjacent tiles, but with wraparound
     */
    @Test
    public void testGetPrincipalDirection_wraparound_simple1() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile topLeft = new Tile(0, 0);
        Tile bottomRight = new Tile(9, 9);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(topLeft, bottomRight));
        assertEquals(Aim.WEST, map.getPrincipalDirection(topLeft, new Tile(0, 9)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(bottomRight, topLeft));
        assertEquals(Aim.EAST, map.getPrincipalDirection(bottomRight, new Tile(9, 0)));
    }

    /**
     * Test getPrincipalDirection() with tiles that are more than 1 step away, but that have only 1 direction component;
     * this time with wraparound
     */
    @Test
    public void testGetPrincipalDirection_wraparound_simple2() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile topLeft = new Tile(0, 0);
        Tile bottomRight = new Tile(9, 9);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(topLeft, new Tile(8, 0)));
        assertEquals(Aim.WEST, map.getPrincipalDirection(topLeft, new Tile(0, 8)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(bottomRight, new Tile(1, 9)));
        assertEquals(Aim.EAST, map.getPrincipalDirection(bottomRight, new Tile(9, 1)));
    }

    /**
     * Test getPrincipalDirection() with the diagonal neighbors. The results are biased N>S>E>W
     */
    @Test
    public void testGetPrincipalDirection_2equalComponents() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile center = new Tile(5, 5);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(center, new Tile(4, 4)));
        assertEquals(Aim.NORTH, map.getPrincipalDirection(center, new Tile(4, 6)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(center, new Tile(6, 4)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(center, new Tile(6, 6)));
    }

    /**
     * Test getPrincipalDirection() with 2 unequal direction components
     */
    @Test
    public void testGetPrincipalDirection_2Components() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile center = new Tile(5, 5);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(center, new Tile(2, 4)));
        assertEquals(Aim.WEST, map.getPrincipalDirection(center, new Tile(4, 2)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(center, new Tile(8, 6)));
        assertEquals(Aim.EAST, map.getPrincipalDirection(center, new Tile(6, 8)));

    }

    /**
     * Test getPrincipalDirection() with 2 unequal direction components and with wraparound
     */
    @Test
    public void testGetPrincipalDirection_2Components_wraparound() {
        SearchableMap map = new SimpleUnitTestMap(10, 10);
        Tile topLeft = new Tile(0, 0);
        Tile bottomRight = new Tile(9, 9);
        assertEquals(Aim.NORTH, map.getPrincipalDirection(topLeft, new Tile(6, 8)));
        assertEquals(Aim.WEST, map.getPrincipalDirection(topLeft, new Tile(9, 6)));
        assertEquals(Aim.SOUTH, map.getPrincipalDirection(bottomRight, new Tile(2, 8)));
        assertEquals(Aim.EAST, map.getPrincipalDirection(bottomRight, new Tile(0, 2)));
    }
}
