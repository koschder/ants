package ants.strategy.rules;

import java.util.Map;

import ants.state.Ants;
import ants.tasks.Task;
import ants.tasks.Task.Type;

public class PopulationSizeRule extends BaseResourceAllocationRule {

    @Override
    public void allocateResources(Map<Type, Task> tasks) {
        if (Ants.getPopulation().getMyAnts().size() < 10) {
            incrementResources(tasks, Type.GATHER_FOOD, 50, Type.ATTACK_HILLS, Type.COMBAT);
        }
    }
}
