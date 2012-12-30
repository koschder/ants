package ants.strategy.rules;

import ants.state.Ants;
import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

public class RelativeInfluenceRule extends BaseResourceAllocationRule {
    private InfluenceMap influence;

    public RelativeInfluenceRule(InfluenceMap influence) {
        this.influence = influence;
    }

    @Override
    /**
     * if our in influence is the highest we force our aggressiveness
     */
    public void allocateResources() {
        if (influence.getTotalInfluence(0) > influence.getTotalOpponentInfluence()) {
            incrementResources(Type.EXPLORE, Ants.getProfile().getExplore_DominantPositionBoost(), Type.GATHER_FOOD);
            incrementResources(Type.ATTACK_HILLS, Ants.getProfile().getAttackHills_DominantPositionBoost(),
                    Type.GATHER_FOOD);
        }
    }

}
