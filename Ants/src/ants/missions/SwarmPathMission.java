package ants.missions;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Tile;

/**
 * 
 * @author kases1, kustl1
 * @deprecated Did not work as hoped, was abandoned
 */
public class SwarmPathMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.SWARM);

    ConcentrateMission cm;
    public List<Tile> path = new ArrayList<Tile>();

    public SwarmPathMission(Ant ant, List<Tile> p, int antAmount) {
        path = p;
        cm = new ConcentrateMission(path.get(0), antAmount, 25);
        moveConcentratePoint();
    }

    @Override
    protected String isSpecificMissionValid() {
        return cm.isSpecificMissionValid();
    }

    @Override
    public void execute() {

        moveConcentratePoint();
        cm.execute();
    }

    private void moveConcentratePoint() {

        if (cm.getOffSetPoint() == null || cm.getConcentratePoint() == null || path.size() == 0)
            return;

        int manhattan = Ants.getWorld().manhattanDistance(cm.getOffSetPoint(), cm.getConcentratePoint());

        // if (cm.isComplete()) {
        if (manhattan < 2) {
            int moveContcentratePoint = Math.min(2, path.size() - 1);
            LOGGER.info("MulitAntPathMission_Pathsublist from %s to %s ", path,
                    path.subList(moveContcentratePoint, path.size()));
            path = path.subList(moveContcentratePoint, path.size());

            cm.setTroopPoint(path.get(0));
            LOGGER.info("MulitAntPathMission_TroopPoint is now %s", cm.getConcentratePoint());
        } else {
            LOGGER.info("MulitAntPathMission_TroopPoint %s NOT changed now. Manhattan is  %s",
                    cm.getConcentratePoint(), manhattan);
        }

    }

    @Override
    public boolean isComplete() {

        return path.size() < 1 && cm.isComplete();
    }

    public void setup() {
        cm.setup();
        super.setup();
    }

    protected boolean checkAnts() {
        return false;
    }

    @Override
    public Type getTaskType() {
        return Type.SWARMPATH;
    }

}
