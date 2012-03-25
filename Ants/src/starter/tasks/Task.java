package starter.tasks;

import java.util.Map;

import starter.Ants;
import starter.Tile;

public interface Task {
    public void perform();

    public void setup(Ants ants, Map<Tile, Tile> orders);
}
