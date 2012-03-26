package starter.tasks;

import starter.Ants;

public interface Task {
    public void perform();

    public void setup(Ants ants);
}
