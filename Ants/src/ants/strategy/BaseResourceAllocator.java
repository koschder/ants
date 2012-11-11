package ants.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.state.Ants;
import ants.strategy.rules.ResourceAllocationRule;
import ants.tasks.Task;

public abstract class BaseResourceAllocator {

    Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);
    protected List<ResourceAllocationRule> rules = new ArrayList<ResourceAllocationRule>();
    protected Map<Task.Type, Task> tasks;

    public void allocateResources() {
        for (ResourceAllocationRule rule : rules) {
            rule.allocateResources(tasks);
        }
        LOGGER.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());
        for (Entry<Task.Type, Task> entry : tasks.entrySet()) {
            LOGGER.info("Allocated %s percent of ants to task %s", entry.getValue().getMaxResources(), entry.getKey());
        }

    }

    public void setRules(List<ResourceAllocationRule> rules) {
        this.rules = rules;
    }

}
