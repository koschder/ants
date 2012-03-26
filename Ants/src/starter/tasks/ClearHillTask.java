package starter.tasks;

import starter.Aim;
import starter.Tile;

public class ClearHillTask extends BaseTask {

    @Override
    public void perform() {
        // unblock hills
        for (Tile myHill : ants.getMyHills()) {
            if (ants.getMyAnts().contains(myHill) && !orders.containsValue(myHill)) {
                for (Aim direction : Aim.values()) {
                    if (doMoveDirection(myHill, direction)) {
                        break;
                    }
                }
            }
        }

    }

}
