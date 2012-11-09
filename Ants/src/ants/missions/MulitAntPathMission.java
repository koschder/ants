package ants.missions;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;

public class MulitAntPathMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CONCENTRATE);

    ConcentrateMission cm;
    public List<Tile> path = new ArrayList<Tile>();

    public MulitAntPathMission(Ant ant, List<Tile> p) {
        path = p;
        cm = new ConcentrateMission(path.get(0), 8);
        moveConcentratePoint();
    }

    @Override
    protected boolean isSpecificMissionValid() {
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

        if (manhattan < 2) {
            int moveContcentratePoint = Math.min(1, path.size() - 1);
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

        return path.size() < 4;
    }

    public void setup() {
        cm.setup();
        for (Ant ant : this.ants) {
            ant.setup();
        }
    }

}
