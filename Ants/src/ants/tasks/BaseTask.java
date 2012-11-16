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
    private int maxAnts = Integer.MAX_VALUE;
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
                    .getSimpleName(), this.maxResources, this.allocatedAnts, this.maxAnts);
        }
    }

    @Override
    public void setMaxResources(Integer maxResources) {
        this.maxResources = maxResources;
        this.maxAnts = (int) Math.ceil(Ants.getPopulation().getMyAnts().size() * (this.maxResources / 100.0));
    }

    @Override
    public Integer getMaxResources() {
        return this.maxResources;
    }

    @Override
    public Integer getMaxAnts() {
        return this.maxAnts;
    }

    protected void doPerform() {

    }

    protected void addMission(Mission mission) {
        if (allocatedAnts >= maxAnts)
            throw new InsufficientResourcesException();

        Ants.getOrders().addMission(mission);
        allocatedAnts++;
    }
}