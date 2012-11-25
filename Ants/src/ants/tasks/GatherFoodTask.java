package ants.tasks;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.missions.GatherFoodMission;
import ants.state.Ants;

/**
 * Searches for food and sends our ants to gather it.
 * 
 * @author kases1,kustl1
 * 
 */
public class GatherFoodTask extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.FOOD);

    @Override
    public void doPerform() {
        if (Ants.getAnts().getTurn() == 1) {
            addMission(new GatherFoodMission());
        }
    }

    @Override
    public Type getType() {
        return Type.GATHER_FOOD;
    }
}
