package ants.strategy;

import java.util.*;
import java.util.Map.Entry;

import logging.*;
import ants.LogCategory;
import ants.state.*;
import ants.strategy.rules.*;
import ants.tasks.*;
import ants.tasks.Task.Type;
import api.strategy.*;

public class ResourceAllocator {
    Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);
    private List<ResourceAllocationRule> rules = new ArrayList<ResourceAllocationRule>();

    private Map<Task.Type, Task> tasks;

    public ResourceAllocator(Map<Type, Task> tasks, InfluenceMap influence) {
        this.tasks = tasks;
        // init the allocation with evenly distributed default values
        this.tasks.get(Type.GATHER_FOOD).setMaxResources(25);
        this.tasks.get(Type.ATTACK_HILLS).setMaxResources(25);
        this.tasks.get(Type.COMBAT).setMaxResources(25);
        this.tasks.get(Type.EXPLORE).setMaxResources(25);

        rules.add(new PopulationSizeRule());
        rules.add(new RelativeInfluenceRule(influence));
        rules.add(new PercentExploredRule());
    }

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
