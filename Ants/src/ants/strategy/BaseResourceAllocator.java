package ants.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.missions.Mission;
import ants.state.Ants;
import ants.strategy.rules.ResourceAllocationRule;
import ants.tasks.Task.Type;

public abstract class BaseResourceAllocator {

    Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);
    protected List<ResourceAllocationRule> rules = new ArrayList<ResourceAllocationRule>();

    public void allocateResources() {
        Map<Type, Integer> antsOnMission = new HashMap<Type, Integer>();
        for (Mission mission : Ants.getOrders().getMissions()) {
            Integer ants = antsOnMission.get(mission.getTaskType());
            if (ants == null)
                antsOnMission.put(mission.getTaskType(), mission.getAnts().size());
            else
                antsOnMission.put(mission.getTaskType(), ants + mission.getAnts().size());
        }
        for (Type type : antsOnMission.keySet()) {
            LOGGER.debug("%s ants currently on mission of type %s", antsOnMission.get(type), type);
        }
        for (ResourceAllocationRule rule : rules) {
            rule.allocateResources();
        }
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
