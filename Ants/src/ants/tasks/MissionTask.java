package ants.tasks;

import java.util.*;

import logging.*;
import ants.LogCategory;
import ants.entities.*;
import ants.missions.*;
import ants.state.*;

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

        Set<Mission> missions = Ants.getOrders().getMissions();
        LOGGER.debug("Current mission count: %s", missions.size());
        for (Iterator<Mission> it = missions.iterator(); it.hasNext();) {
            Mission mission = it.next();
            // LOGGER.debug("mission: %s", mission);
            if (mission.isComplete()) {
                removeAnts(mission);
                it.remove();
                LOGGER.debug("Mission removed, its complete: %s", mission);
                continue;
            }
            final String valid = mission.isValid();
            if (valid == null) {
                mission.execute();
                LOGGER.debug("Mission performed: %s", mission);
            } else {
                LOGGER.debug("Mission %s not valid because: %s. Mission is removed.", mission, valid);
                removeAnts(mission);
                it.remove();
            }
        }

    }

    private void removeAnts(Mission mission) {
        for (Ant ant : mission.getAnts()) {
            Ants.getPopulation().removeEmployedAnt(ant);
        }
    }
}
