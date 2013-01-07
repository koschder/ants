package ants.strategy.rules;

import ants.state.Ants;
import ants.tasks.Task.Type;

/**
 * This rule increments the resources for attacking hills when the half-time is reached.
 * 
 * @author kases1, kustl1
 * 
 */
public class HalfTimeReachedRule extends BaseResourceAllocationRule {

    @Override
    public void allocateResources() {
        // half time reached
        if (Ants.getAnts().getTurns() / 2 < Ants.getAnts().getTurn()) {
            incrementResources(Type.ATTACK_HILLS, Ants.getProfile().getAttackHills_HalfTimeBoost(), Type.EXPLORE,
                    Type.GATHER_FOOD);
        }
    }

}
