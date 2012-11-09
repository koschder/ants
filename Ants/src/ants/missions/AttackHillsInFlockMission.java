package ants.missions;

import ants.state.*;
import api.entities.*;

public class AttackHillsInFlockMission extends BaseMission {

    private Mission partialMission;
    private Tile hill;

    public AttackHillsInFlockMission(Tile hill, Tile rallyPoint, int a, int attractionDistance) {
        partialMission = new ConcentrateMission(rallyPoint, a, attractionDistance);
        this.hill = hill;
    }

    @Override
    public boolean isComplete() {
        return partialMission.isComplete() && partialMission instanceof FlockMission;
    }

    @Override
    public void execute() {
        if (partialMission.isComplete() && partialMission instanceof ConcentrateMission) {
            partialMission = new FlockMission(hill, partialMission.getAnts());
        }
        partialMission.execute();
    }

    /***
     * mission is as long vaild, as long the enemy hill exists.
     */
    @Override
    protected boolean isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(hill))
            return false;
        return true;
    }

    @Override
    public void setup() {
        super.setup();
        partialMission.setup();
    }

    public Tile getHill() {
        return hill;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": Hill " + hill + ", Ants " + partialMission.getAnts();
    }

    @Override
    protected boolean isCheckAnts() {
        return false;
    }

}
