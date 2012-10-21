package ants.strategy.rules;

import java.util.Map;

import ants.tasks.Task;
import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

public class RelativeInfluenceRule extends BaseResourceAllocationRule {
    private InfluenceMap influence;

    public RelativeInfluenceRule(InfluenceMap influence) {
        this.influence = influence;
    }

    @Override
    public void allocateResources(Map<Type, Task> tasks) {
        if (influence.getTotalInfluence(0) > influence.getTotalOpponentInfluence()) {
            incrementResources(tasks, Type.EXPLORE, 5, Type.GATHER_FOOD);
            incrementResources(tasks, Type.ATTACK_HILLS, 2, Type.GATHER_FOOD);
            incrementResources(tasks, Type.COMBAT, 2, Type.GATHER_FOOD);
        }
    }

}
