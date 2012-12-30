package unittest.ants.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ants.entities.Ant;
import ants.profile.Profile;
import ants.state.Ants;
import ants.state.Orders;
import ants.state.Population;
import ants.state.World;
import ants.strategy.BaseResourceAllocator;
import ants.strategy.ResourceAllocator;
import ants.tasks.AttackHillsTask;
import ants.tasks.ExploreTask;
import ants.tasks.GatherFoodTask;
import ants.tasks.Task;
import ants.tasks.Task.Type;
import api.entities.Tile;

import static org.junit.Assert.*;

public class ResourceAllocatorTest {

    private BaseResourceAllocator resourceAllocator;
    private Map<Task.Type, Task> tasks = new HashMap<Task.Type, Task>();
    private StubInfluenceMap influence = new StubInfluenceMap();

    @Before
    public void setUp() {
        tasks.put(Type.GATHER_FOOD, new GatherFoodTask());
        tasks.put(Type.ATTACK_HILLS, new AttackHillsTask());
        tasks.put(Type.EXPLORE, new ExploreTask());
    }

    @Test
    public void testAllocateResources() {
        initResourceAllocator(10);
        Ants.setWorld(getWorld(50));
        influence.setTotalOpponentInfluence(1000);
        influence.setTotalInfluence(0, 2000);

        final Integer foodResources = Ants.getPopulation().getMaxResources(Type.GATHER_FOOD);
        // method under test
        resourceAllocator.allocateResources();

        assertEquals(Integer.valueOf(foodResources + 9), Ants.getPopulation().getMaxResources(Type.GATHER_FOOD));
    }

    private void initResourceAllocator(int population) {
        Ants.setPopulation(getPopulation(population));
        Ants.setOrders(new Orders());
        Ants.setProfile(new Profile(null));
        resourceAllocator = new ResourceAllocator(influence);
    }

    private Population getPopulation(final int myAnts) {
        return new Population() {
            @Override
            public Set<Ant> getMyAnts() {
                Set<Ant> ants = new HashSet<Ant>();
                for (int i = 0; i <= myAnts; i++) {
                    ants.add(new Ant(new Tile(i, i), 0));
                }
                return ants;
            }
        };
    }

    private World getWorld(final int percentVisibleTiles) {
        return new World() {
            @Override
            public int getVisibleTilesPercent() {
                return percentVisibleTiles;
            }
        };
    }
}
