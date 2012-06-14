package ants.tasks;

/**
 * Interface for a task to be performed each turn.
 * 
 * @author kases1,kustl1
 * 
 */
public interface Task {

    /**
     * Performs the task; called each turn.
     */
    public void perform();

    /**
     * Prepare the task; called at the beginning of each turn.
     */
    public void setup();
}
