package ants.tactics;

import api.entities.Tile;
import api.entities.Unit;

public interface CombatPositioning {
    public Tile getNextTile(Unit u);
}
