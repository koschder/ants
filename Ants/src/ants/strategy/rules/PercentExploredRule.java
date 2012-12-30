package ants.strategy.rules;

import ants.state.Ants;
import ants.tasks.Task.Type;

public class PercentExploredRule extends BaseResourceAllocationRule {

    @Override
    /**
     * if not much is visible we force the explore task
     */
    public void allocateResources() {
        final int visibleTilesPercent = Ants.getWorld().getVisibleTilesPercent();
        if (visibleTilesPercent < Ants.getProfile().getExplore_ForceThresholdPercent()) {
            int increment = Math.round((100 - visibleTilesPercent) * Ants.getProfile().getExplore_ForceGain());
            Type[] tasksToDecrement = new Type[] { Type.ATTACK_HILLS, Type.GATHER_FOOD, Type.DEFEND_HILL };
            increment -= increment % tasksToDecrement.length;
            incrementResources(Type.EXPLORE, increment, tasksToDecrement);
        }
    }

}
