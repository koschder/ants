package ants.tasks;

/**
 * Interface for a task to be performed each turn.
 * 
 * @author kases1, kustl1
 * 
 */
public interface Task {

    public enum Type {
        ATTACK_HILLS,
        CLEAR_HILL,
        CLUSTERING,
        COMBAT,
        DEFEND_AREA,
        EXPLORE,
        GATHER_FOOD,
        MISSION,
        VALIDATE_ORDERS,
        CONCENTRATE_ANTS,
        FLOCK,
        SWARMPATH,
        DEFEND_HILL;
    }

    /**
     * Performs the task; called each turn
     * 
     */
    public void perform();

    /**
     * Prepare the task; called at the beginning of each turn.
     */
    public void setup();

    public Type getType();
}
