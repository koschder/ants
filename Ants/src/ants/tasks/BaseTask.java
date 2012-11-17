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

    private int allocatedAnts = 0;

    @Override
    public void setup() {
    }

    @Override
    public void perform() {
        allocatedAnts = 0;
        try {
            doPerform();
        } catch (InsufficientResourcesException e) {
            LOGGER.info("Task %s reached its resource limit: %s percent (alloc: %s, max: %s) ", getClass()
                    .getSimpleName(), Ants.getPopulation().getMaxResources(getType()), this.allocatedAnts, Ants
                    .getPopulation().getMaxAnts(getType()));
        }
    }

    protected abstract void doPerform();

    protected void addMission(Mission mission) {
        if (allocatedAnts >= Ants.getPopulation().getMaxAnts(getType()))
            throw new InsufficientResourcesException();

        Ants.getOrders().addMission(mission);
        allocatedAnts++;
    }
}