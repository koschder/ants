package ants.missions;

import ants.entities.Ant;
import ants.entities.Move;

public interface Mission {
    public boolean isComplete();

    public boolean isValid();

    public void execute();

    public Ant getAnt();

    public Move getLastMove();
}
