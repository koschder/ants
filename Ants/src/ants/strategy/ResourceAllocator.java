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
import ants.strategy.rules.HalfTimeReachedRule;
import ants.strategy.rules.PercentExploredRule;
import ants.strategy.rules.PopulationSizeRule;
import ants.strategy.rules.RelativeInfluenceRule;
import ants.strategy.rules.ResourceAllocationRule;
import ants.tasks.Task.Type;
import api.strategy.InfluenceMap;

/**
 * The ResourceAllocator class is responsible for dividing up the available resources between the different tasks
 * 
 * @author kases1, kustl1
 * 
 */
public class ResourceAllocator {
    Logger LOGGER = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);

    private List<ResourceAllocationRule> rules = new ArrayList<ResourceAllocationRule>();

    /**
     * Default constructor
     * 
     * @param influence
     */
    public ResourceAllocator(InfluenceMap influence) {
        // init for all task types
        for (Type type : Type.values()) {
            if (Ants.getPopulation().getMaxResources(type) == null)
                Ants.getPopulation().setMaxResources(type, Integer.MAX_VALUE);
        }
        // init the allocation with evenly distributed default values
        Ants.getPopulation().setMaxResources(Type.GATHER_FOOD, Ants.getProfile().getDefaultAllocation_GatherFood());
        Ants.getPopulation().setMaxResources(Type.ATTACK_HILLS, Ants.getProfile().getDefaultAllocation_AttackHills());
        Ants.getPopulation().setMaxResources(Type.EXPLORE, Ants.getProfile().getDefaultAllocation_Explore());
        Ants.getPopulation().setMaxResources(Type.DEFEND_HILL, Ants.getProfile().getDefaultAllocation_DefendHills());

        rules.add(new PopulationSizeRule());
        rules.add(new RelativeInfluenceRule(influence));
        rules.add(new PercentExploredRule());
        rules.add(new HalfTimeReachedRule());
    }

    /**
     * This method allocates the available resources to the tasks according to the configured rules.
     */
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
