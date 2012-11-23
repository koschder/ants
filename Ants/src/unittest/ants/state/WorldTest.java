package unittest.ants.state;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ants.entities.Ant;
import ants.state.World;
import api.entities.Tile;

import static org.junit.Assert.*;

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
