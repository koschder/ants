package ants.strategy.rules;


import ants.state.Ants;
import ants.tasks.Task.Type;

public class HalfTimeReachedRule extends BaseResourceAllocationRule {

    @Override
    public void allocateResources() {
        // half time reached
        if (Ants.getAnts().getTurns() / 2 < Ants.getAnts().getTurn()) {
            incrementResources(Type.ATTACK_HILLS, 20, Type.EXPLORE, Type.GATHER_FOOD);
        }
    }

}
