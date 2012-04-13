package starter.tasks;

import starter.Aim;
import starter.Ant;
import starter.Ants;

public class ClearHillTask extends BaseTask {

    @Override
    public void perform() {
        for (Ant ant : Ants.getAnts().getMyUnemployedAnts()) {
            for (Aim aim : Aim.values()) {
                if (Ants.getAnts().putOrder(ant, aim))
                    return;
            }
        }

    }

}
