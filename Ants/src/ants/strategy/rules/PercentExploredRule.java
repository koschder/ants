package ants.strategy.rules;

import java.util.Map;

import ants.state.Ants;
import ants.tasks.Task;
import ants.tasks.Task.Type;

public class PercentExploredRule extends BaseResourceAllocationRule {

    @Override
    public void allocateResources(Map<Type, Task> tasks) {
        final int visibleTilesPercent = Ants.getWorld().getVisibleTilesPercent();
        if (visibleTilesPercent < 80) {
            int increment = Math.round((100 - visibleTilesPercent) / 4);
            Type[] tasksToDecrement = new Type[] { Type.ATTACK_HILLS, Type.GATHER_FOOD, Type.COMBAT };
            increment -= increment % tasksToDecrement.length;
            incrementResources(tasks, Type.EXPLORE, increment, tasksToDecrement);
        }
    }

}
