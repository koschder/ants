package unittest.ants.strategy;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ants.entities.Ant;
import ants.state.Ants;
import ants.state.Population;
import ants.strategy.ResourceAllocator;
import ants.tasks.AttackHillsTask;
import ants.tasks.CombatTask;
import ants.tasks.ExploreTask;
import ants.tasks.GatherFoodTask;
import ants.tasks.Task;
import ants.tasks.Task.Type;
import api.Tile;

public class ResourceAllocatorTest {

    private ResourceAllocator resourceAllocator;
    private Map<Task.Type, Task> tasks = new HashMap<Task.Type, Task>();
    private StubInfluenceMap influence = new StubInfluenceMap();

    @Before
    public void setUp() {
        Ants.setPopulation(getPopulation(10));
        tasks.put(Type.GATHER_FOOD, new GatherFoodTask());
        tasks.put(Type.ATTACK_HILLS, new AttackHillsTask());
        tasks.put(Type.COMBAT, new CombatTask());
        tasks.put(Type.EXPLORE, new ExploreTask());
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

    @Test
    public void testAllocateResources() {
        influence.setTotalOpponentInfluence(1000);
        influence.setTotalInfluence(0, 2000);

        final Integer foodResources = tasks.get(Type.GATHER_FOOD).getMaxResources();
        // method under test
        resourceAllocator.allocateResources();

        assertEquals(Integer.valueOf(foodResources - 9), tasks.get(Type.GATHER_FOOD).getMaxResources());
    }

}
