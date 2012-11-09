package unittest.ants.state;

import java.util.*;

import org.junit.*;

import ants.entities.*;
import ants.state.*;
import api.entities.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorldTest {
    private World world;

    @Before
    public void setup() {
        world = new World(40, 40, 55, 5, 1);
    }

    @Test
    public void testGetVisibleTilesPercent() {
        world.updateVision(Arrays.asList(new Ant(new Tile(1, 1), 0), new Ant(new Tile(20, 20), 0)));
        assertEquals(22, world.getVisibleTilesPercent());
    }

    @Test
    public void testGetVisibleTiles() {
        final Tile tile = new Tile(10, 10);
        List<Tile> tiles = world.getVisibleTiles(new Ant(tile, 0));
        assertEquals(177, tiles.size());
        assertTrue(tiles.contains(tile));
    }
}
