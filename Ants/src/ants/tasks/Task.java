package ants.tasks;

/**
 * Interface for a task to be performed each turn.
 * 
 * @author kases1, kustl1
 * 
 */
public interface Task {

    /**
     * Enumeration for the different types of tasks. This is used when deciding how many ants to assign to specific
     * tasks.
     * 
     * @author kases1, kustl1
     * 
     */
    public enum Type {
        ATTACK_HILLS,
        CLEAR_HILL,
        CLUSTERING,
        COMBAT,
        EXPLORE,
        GATHER_FOOD,
        MISSION,
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

    /**
     * 
     * @return the type of this task
     */
    public Type getType();
}
