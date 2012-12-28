package ants.strategy.rules;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.state.Ants;
import ants.tasks.Task.Type;

public abstract class BaseResourceAllocationRule implements ResourceAllocationRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);

    protected void incrementResources(Type taskType, int increment, Type... tasksToDecrement) {
        // ensure increment is evenly distributable
        increment += increment % tasksToDecrement.length;
        LOGGER.debug("%s: Incrementing Resources for %s by %s", getClass().getSimpleName(), taskType, increment);
        final int decrementPerTask = increment / tasksToDecrement.length;
        for (Type type : tasksToDecrement) {
            int maxResources = Ants.getPopulation().getMaxResources(type);
            int newMax = maxResources - decrementPerTask;
            if (newMax < 0) {
                increment = increment + newMax;
                newMax = 0;
            }
            Ants.getPopulation().setMaxResources(type, newMax);
            LOGGER.debug("%s: Decrementing Resources for %s by %s, new max is %s", getClass().getSimpleName(), type,
                    decrementPerTask, newMax);
        }
        final Integer currentMaxResources = Ants.getPopulation().getMaxResources(taskType);
        Ants.getPopulation().setMaxResources(taskType, currentMaxResources + increment);
    }

}
