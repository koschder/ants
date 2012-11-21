package ants.strategy.rules;

import ants.state.*;
import ants.tasks.Task.Type;

public class PopulationSizeRule extends BaseResourceAllocationRule {

    @Override
    /**
     * if our population is small we force the gather food task
     */
    public void allocateResources() {
        final int popSize = Ants.getPopulation().getMyAnts().size();
        if (popSize < 10) {
            incrementResources(Type.GATHER_FOOD, 51, Type.ATTACK_HILLS, Type.COMBAT, Type.DEFEND_HILL);
        }
        if (popSize < 8 * Ants.getWorld().getMyHills().size()) {
            incrementResources(Type.GATHER_FOOD, 5, Type.DEFEND_HILL);
        }
    }
}
