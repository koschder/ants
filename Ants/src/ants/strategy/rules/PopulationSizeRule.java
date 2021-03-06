package ants.strategy.rules;

import ants.state.Ants;
import ants.tasks.Task.Type;

/**
 * This rule increments the resources for gathering food depending on the size of our population.
 * 
 * @author kases1, kustl1
 * 
 */
public class PopulationSizeRule extends BaseResourceAllocationRule {

    @Override
    /**
     * if our population is small we force the gather food task
     */
    public void allocateResources() {
        final int popSize = Ants.getPopulation().getMyAnts().size();
        if (popSize < 25) {
            incrementResources(Type.GATHER_FOOD, 20, Type.ATTACK_HILLS, Type.DEFEND_HILL);
        } else if (popSize < 10) {
            incrementResources(Type.GATHER_FOOD, 50, Type.ATTACK_HILLS, Type.DEFEND_HILL);
        }
        if (popSize < 8 * Ants.getWorld().getMyHills().size()) {
            incrementResources(Type.GATHER_FOOD, 5, Type.DEFEND_HILL);
        }
    }
}
