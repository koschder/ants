package ants.missions;

import ants.state.Ants;
import api.entities.Tile;

public class AttackHillsInFlockMission extends BaseMission {

    private Mission partialMission;
    private Tile hill;
    private Tile startPoint;

    public AttackHillsInFlockMission(Tile hill, Tile startPoint, Tile rallyPoint, int a, int attractionDistance) {
        partialMission = new ConcentrateMission(rallyPoint, a, attractionDistance);
        this.hill = hill;
        this.startPoint = startPoint;
    }

    @Override
    public boolean isComplete() {
        return partialMission.isComplete() && partialMission instanceof FlockMission;
    }

    @Override
    public void execute() {
        if (partialMission.isComplete() && partialMission instanceof ConcentrateMission) {
            partialMission = new FlockMission(hill, Ants.getOrders().getAnts(partialMission));
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

    public Tile getStartPoint() {
        return startPoint;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": Hill " + hill + ", Ants " + Ants.getOrders().getAnts(partialMission);
    }

    @Override
    protected boolean isCheckAnts() {
        return false;
    }

}
