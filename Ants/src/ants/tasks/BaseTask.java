package ants.tasks;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.missions.Mission;
import ants.state.Ants;

/**
 * Base implementation of a Task, mainly so Tasks that have no need for a setup method don't need to implement it.
 * 
 * @author kases1,kustl1
 * 
 */
public abstract class BaseTask implements Task {
    Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_TASKS);

    private class InsufficientResourcesException extends RuntimeException {

        private static final long serialVersionUID = 1L;

    }

    private int maxResources = Integer.MAX_VALUE;
    private int allocatedResources = 0;

    @Override
    public void setup() {
    }

    @Override
    public void perform(Integer maxResources) {
        this.maxResources = maxResources;
        allocatedResources = 0;
        try {
            doPerform();
        } catch (InsufficientResourcesException e) {
            LOGGER.info("Task %s reached its resource limit: %s", getClass().getSimpleName(), this.maxResources);
        }
    }

    protected void doPerform() {

    }

    protected void addMission(Mission mission) {
        if (allocatedResources >= maxResources)
            throw new InsufficientResourcesException();

        Ants.getOrders().addMission(mission);
        allocatedResources++;
    }
}