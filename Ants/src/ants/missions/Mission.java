package ants.missions;

import java.util.List;

import ants.entities.Ant;
import ants.tasks.Task.Type;

/**
 * This interface defines which method must be implemented by a mission
 * 
 * @author kases1, kustl1
 * 
 */
public interface Mission {

    /**
     * 
     * @return is the mission complete, i.e. has it reached its goal?
     */
    public boolean isComplete();

    /**
     * 
     * @return is the mission still valid, or have the conditions changed?
     */
    public String isValid();

    /**
     * 
     * @return has the mission been abandoned?
     */
    public boolean isAbandoned();

    /**
     * Perform setup duties. This is called every turn befor execute()
     */
    public void setup();

    /**
     * Execute the mission. This is called every turn.
     */
    public void execute();

    /**
     * 
     * @return the ants currently occupied by this mission
     */
    public List<Ant> getAnts();

    /**
     * 
     * @return the Task.Type this mission belongs to
     */
    public Type getTaskType();

}
