package ants.tasks;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.missions.Mission;
import ants.state.Ants;
import ants.state.InsufficientResourcesException;

/**
 * Base implementation of a Task, mainly so Tasks that have no need for a setup method don't need to implement it.
 * 
 * @author kases1,kustl1
 * 
 */
public abstract class BaseTask implements Task {
    Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_TASKS);

    @Override
    public void setup() {
    }

    @Override
    public void perform() {
        try {
            doPerform();
        } catch (InsufficientResourcesException e) {
            LOGGER.info("Task %s reached its resource limit: %s percent (max: %s) ", getClass().getSimpleName(), Ants
                    .getPopulation().getMaxResources(getType()), Ants.getPopulation().getMaxAnts(getType()));
        }
    }

    protected abstract void doPerform();

    protected void addMission(Mission mission) {
        final int antsOnMission = mission.getAnts().size();
        if (!Ants.getPopulation().isNumberOfAntsAvailable(getType(), antsOnMission))
            throw new InsufficientResourcesException("Not enough ants available to start mission");

        Ants.getOrders().addMission(mission);
        Ants.getPopulation().incrementUsedResources(getType(), antsOnMission);
    }
}