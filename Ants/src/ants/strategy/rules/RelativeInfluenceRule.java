package ants.strategy.rules;


import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

public class RelativeInfluenceRule extends BaseResourceAllocationRule {
    private InfluenceMap influence;

    public RelativeInfluenceRule(InfluenceMap influence) {
        this.influence = influence;
    }

    @Override
    /***
     * if our in influence is the highest we force our aggressivity
     */
    public void allocateResources() {
        if (influence.getTotalInfluence(0) > influence.getTotalOpponentInfluence()) {
            incrementResources(Type.EXPLORE, 5, Type.GATHER_FOOD);
            incrementResources(Type.ATTACK_HILLS, 2, Type.GATHER_FOOD);
            incrementResources(Type.COMBAT, 2, Type.GATHER_FOOD);
        }
    }

}
