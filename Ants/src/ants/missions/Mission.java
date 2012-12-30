package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.tasks.Task.Type;

/**
 * defines which method must be implemented by a mission
 * 
 * @author kases1, kustl1
 * 
 */
public interface Mission {
    public boolean isComplete();

    public String isValid();

    public void execute();

    /**
     * Mission was abandoned, no move was done this step.
     * 
     * @return
     */
    public boolean isAbandoned();

    public void setup();

    public List<Ant> getAnts();

    public Type getTaskType();

}
