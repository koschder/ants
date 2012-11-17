package ants.strategy.rules;


import ants.state.Ants;
import ants.tasks.Task.Type;

public class PopulationSizeRule extends BaseResourceAllocationRule {

    @Override
    /**
     * if our population is small we force the gather food task
     */
    public void allocateResources() {
        if (Ants.getPopulation().getMyAnts().size() < 10) {
            incrementResources(Type.GATHER_FOOD, 51, Type.ATTACK_HILLS, Type.COMBAT, Type.DEFEND_HILL);
        }
    }
}
