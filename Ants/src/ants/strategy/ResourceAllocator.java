package ants.strategy;

import java.util.Map;

import ants.strategy.rules.PercentExploredRule;
import ants.strategy.rules.PopulationSizeRule;
import ants.strategy.rules.RelativeInfluenceRule;
import ants.tasks.Task;
import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

public class ResourceAllocator extends BaseResourceAllocator {
    public ResourceAllocator(Map<Type, Task> tasks, InfluenceMap influence) {
        this.tasks = tasks;
        // init the allocation with evenly distributed default values
        this.tasks.get(Type.GATHER_FOOD).setMaxResources(25);
        this.tasks.get(Type.ATTACK_HILLS).setMaxResources(25);
        this.tasks.get(Type.COMBAT).setMaxResources(25);
        this.tasks.get(Type.EXPLORE).setMaxResources(25);

        rules.add(new PopulationSizeRule());
        rules.add(new RelativeInfluenceRule(influence));
        rules.add(new PercentExploredRule());
    }
}
