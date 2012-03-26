package starter.tasks;

import starter.Aim;
import starter.Ant;

public class ClearHillTask extends BaseTask {

    @Override
    public void perform() {
        for (Ant ant : ants.getMyUnemployedAnts()) {
            for (Aim aim : Aim.values()) {
                if (ants.putOrder(ant, aim))
                    return;
            }
        }

    }

}
