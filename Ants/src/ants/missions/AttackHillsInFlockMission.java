package ants.missions;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Tile;

public class AttackHillsInFlockMission extends BaseMission {

    private Mission partialMission;
    private Tile hill;
    private Tile startPoint;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.ATTACK_HILLS_FLOCKED);

    public AttackHillsInFlockMission(Tile hill, Tile startPoint, Tile rallyPoint, int antAmount, int attractionDistance) {
        partialMission = new ConcentrateMission(rallyPoint, antAmount, attractionDistance);
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
            partialMission = new FlockMission(hill, partialMission.getAnts());
            LOGGER.info("ConcentrateMission completed, start with FlockMission % s", partialMission);
        }
        partialMission.execute();
    }

    /**
     * mission is as long valid, as long the enemy hill exists.
     */
    @Override
    protected String isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(hill))
            return "Enemy hill " + hill + " is no longer there";
        return partialMission.isValid();
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
        return getClass().getSimpleName() + ": Hill " + hill + ", PartialMission: " + partialMission;
    }

    @Override
    protected boolean checkAnts() {
        return false;
    }

    @Override
    public Type getTaskType() {
        return Type.ATTACK_HILLS;
    }

}
