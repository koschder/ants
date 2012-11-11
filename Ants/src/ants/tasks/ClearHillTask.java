package ants.tasks;

import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Aim;
import api.entities.Tile;

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
            for (Tile hill : Ants.getWorld().getMyHills()) {
                if (Ants.getWorld().manhattanDistance(ant.getTile(), hill) < 2) {
                    for (Aim aim : Aim.values()) {
                        if (Ants.getWorld().getTile(ant.getTile(), aim).equals(hill))
                            continue; // dont go back to the hill
                        if (Ants.getOrders().issueOrder(ant, aim, getClass().getSimpleName()))
                            break;
                    }
                    break;
                }
            }
        }

    }

}
