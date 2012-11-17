package ants.strategy;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.state.Ants;
import ants.strategy.rules.ResourceAllocationRule;
import ants.tasks.Task.Type;

public abstract class BaseResourceAllocator {

    Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);
    protected List<ResourceAllocationRule> rules = new ArrayList<ResourceAllocationRule>();

    public void allocateResources() {
        for (ResourceAllocationRule rule : rules) {
            rule.allocateResources();
        }
        LOGGER.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());
        for (Type type : Type.values()) {
            final Integer maxResources = Ants.getPopulation().getMaxResources(type);
            if (maxResources < Integer.MAX_VALUE) {
                LOGGER.info("Allocated %s percent of ants to task %s", maxResources, type.name());
            }
        }

    }

    public void setRules(List<ResourceAllocationRule> rules) {
        this.rules = rules;
    }

}
