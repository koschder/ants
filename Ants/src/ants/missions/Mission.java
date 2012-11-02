package ants.missions;

import ants.entities.Ant;
import api.entities.*;

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

    public Ant getAnt();

    public Move getLastMove();

    public void setup();
}
