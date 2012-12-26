package tactics.combat;

import api.entities.Tile;
import api.entities.Unit;

public interface CombatPositioning {
    public Tile getNextTile(Unit u);

    public String getLog();

}
