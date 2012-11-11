package unittest.ants.strategy;

import java.util.*;

import org.junit.*;

import ants.entities.*;
import ants.state.*;
import ants.strategy.*;
import ants.tasks.*;
import ants.tasks.Task.Type;
import api.entities.*;

import static org.junit.Assert.assertEquals;

public class ResourceAllocatorTest {

    private BaseResourceAllocator resourceAllocator;
    private Map<Task.Type, Task> tasks = new HashMap<Task.Type, Task>();
    private StubInfluenceMap influence = new StubInfluenceMap();

    @Before
    public void setUp() {
        tasks.put(Type.GATHER_FOOD, new GatherFoodTask());
        tasks.put(Type.ATTACK_HILLS, new AttackHillsTask());
        tasks.put(Type.COMBAT, new CombatTask());
        tasks.put(Type.EXPLORE, new ExploreTask());
    }

    @Test
    public void testAllocateResources() {
        initResourceAllocator(10);
        Ants.setWorld(getWorld(50));
        influence.setTotalOpponentInfluence(1000);
        influence.setTotalInfluence(0, 2000);

        final Integer foodResources = tasks.get(Type.GATHER_FOOD).getMaxResources();
        // method under test
        resourceAllocator.allocateResources();

        assertEquals(Integer.valueOf(foodResources - 13), tasks.get(Type.GATHER_FOOD).getMaxResources());
    }

    private void initResourceAllocator(int population) {
        Ants.setPopulation(getPopulation(population));
        resourceAllocator = new ResourceAllocator(tasks, influence);
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
