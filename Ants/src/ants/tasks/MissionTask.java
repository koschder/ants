package ants.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.missions.Mission;
import ants.state.Ants;

/**
 * This task is responsible for allowing those ants that are currently following a {@link Mission} to execute the next
 * step of their mission.
 * 
 * @author kases1,kustl1
 * 
 */
public class MissionTask extends BaseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);

    @Override
    public void setup() {
        super.setup();
        for (Mission m : Ants.getOrders().getMissions()) {
            m.setup();
        }
    }

    @Override
    public void doPerform() {
        // declare explicit sortOrder for gathering mission types
        List<Mission> missions = sort(Ants.getOrders().getMissions(), Type.EXPLORE, Type.COMBAT, Type.DEFEND_HILL,
                Type.GATHER_FOOD, Type.ATTACK_HILLS);
        LOGGER.debug("Current mission count: %s", missions.size());
        LOGGER.debug("Missions: %s", missions);
        Ants.getOrders().executeMissions(missions);

    }

    @Override
    public Type getType() {
        return Type.MISSION;
    }

    private List<Mission> sort(Set<Mission> missions, Type... typesInOrder) {
        ArrayList<Mission> sortedMissions = new ArrayList<Mission>(missions);
        Collections.sort(sortedMissions, new MissionComparator(typesInOrder));
        return sortedMissions;
    }

    private class MissionComparator implements Comparator<Mission> {
        private List<Type> types;

        public MissionComparator(Type... typesInOrder) {
            types = Arrays.asList(typesInOrder);
        }

        @Override
        public int compare(Mission o1, Mission o2) {
            final Type taskType1 = o1.getTaskType();
            final Type taskType2 = o2.getTaskType();
            if (types.contains(taskType1) && types.contains(taskType2))
                return types.indexOf(taskType1) - types.indexOf(taskType2);
            // special case handling for types that are not in the list
            else if (!types.contains(taskType1) && !types.contains(taskType2))
                return 0;
            else if (!types.contains(taskType2))
                return -1;
            else
                return 1;
        }

    }
}
