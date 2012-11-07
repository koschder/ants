package ants.missions;

import ants.entities.Ant;
import api.entities.Move;

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

    // TODO remove
    public Ant getAnt();

    public Move getLastMove();

    public void setup();
}
