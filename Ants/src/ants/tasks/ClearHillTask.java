package ants.tasks;

import ants.entities.Ant;
import ants.state.Ants;
import api.Aim;

/**
 * Task that ensures that no ant blocks our hills. This should be executed last, since it just issues a random order for
 * the last of our unemployed ants.
 * 
 * @author kases1,kustl1
 * 
 */
public class ClearHillTask extends BaseTask {

    @Override
    public void doPerform() {
        for (Ant ant : Ants.getPopulation().getMyUnemployedAnts()) {
            for (Aim aim : Aim.values()) {
                if (Ants.getOrders().issueOrder(ant, aim, getClass().getSimpleName()))
                    break;
            }
        }

    }

}
