package ants.strategy.rules;

import java.util.Map;

import ants.state.Ants;
import ants.tasks.Task;
import ants.tasks.Task.Type;

public class HalfTimeReachedRule extends BaseResourceAllocationRule {

    @Override
    public void allocateResources(Map<Type, Task> tasks) {
        // half time reached
        if (Ants.getAnts().getTurns() / 2 < Ants.getAnts().getTurn()) {
            incrementResources(tasks, Type.ATTACK_HILLS, 20, Type.EXPLORE, Type.GATHER_FOOD);
        }
    }

}
