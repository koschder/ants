package ants.tasks;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.state.Ants;

public class ClearHillTask extends BaseTask {

    @Override
    public void perform() {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            for (Aim aim : Aim.values()) {
                if (Ants.getOrders().putOrder(ant, aim))
                    return;
            }
        }

    }

}
