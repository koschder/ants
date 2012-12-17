package ants.strategy;

import ants.state.Ants;
import ants.strategy.rules.HalfTimeReachedRule;
import ants.strategy.rules.PercentExploredRule;
import ants.strategy.rules.PopulationSizeRule;
import ants.strategy.rules.RelativeInfluenceRule;
import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

public class ResourceAllocator extends BaseResourceAllocator {
    public ResourceAllocator(InfluenceMap influence) {
        // init for all task types
        for (Type type : Type.values()) {
            if (Ants.getPopulation().getMaxResources(type) == null)
                Ants.getPopulation().setMaxResources(type, Integer.MAX_VALUE);
        }
        // init the allocation with evenly distributed default values
        Ants.getPopulation().setMaxResources(Type.GATHER_FOOD, 25);
        Ants.getPopulation().setMaxResources(Type.ATTACK_HILLS, 20);
        Ants.getPopulation().setMaxResources(Type.EXPLORE, 25);
        Ants.getPopulation().setMaxResources(Type.DEFEND_HILL, 20);

        rules.add(new PopulationSizeRule());
        rules.add(new RelativeInfluenceRule(influence));
        rules.add(new PercentExploredRule());
        rules.add(new HalfTimeReachedRule());
    }
}
