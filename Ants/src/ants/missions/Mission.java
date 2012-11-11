package ants.missions;

import java.util.List;

import ants.entities.Ant;

/***
 * defines which method must be implemented by a mission
 * 
 * @author kaeserst
 * 
 */
public interface Mission {
    public boolean isComplete();

    public boolean isValid();

    public void execute();

    public void setup();

    public List<Ant> getAnts();

}
