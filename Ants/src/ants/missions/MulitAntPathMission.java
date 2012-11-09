package ants.missions;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Ant;
import api.entities.Tile;

public class MulitAntPathMission extends BaseMission {

    ConcentrateMission cm;
    public List<Tile> path = new ArrayList<Tile>();

    public MulitAntPathMission(Ant ant, List<Tile> p) {
        path = p;
        cm = new ConcentrateMission(path.get(0), 8);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        return cm.isSpecificMissionValid();
    }

    @Override
    public void execute() {
        if (cm.getDiffxToPoint() + cm.getDiffyToPoint() < 10) {
            moveConcentratePoint();
        }
        cm.execute();
    }

    private void moveConcentratePoint() {
        int moveContcentratePoint = 5;
        path = path.subList(moveContcentratePoint, path.size());
        cm.setTroopPoint(path.get(0));
    }

    @Override
    public boolean isComplete() {

        return path.size() < 4;
    }

}
