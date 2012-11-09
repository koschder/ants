package ants.missions;

import java.util.*;

import ants.entities.*;

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
