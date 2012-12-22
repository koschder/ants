package tactics.combat;

import java.util.List;

import api.entities.Tile;
import api.entities.Unit;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class DefendingCombatPositioning extends DefaultCombatPositioning {

    public DefendingCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        super(map, influenceMap, myUnits, enemyUnits, ultimateTarget);
    }

    public DefendingCombatPositioning(Tile ultimateTarget, SearchableUnitMap map, InfluenceMap influenceMap,
            List<Unit> myUnits, List<Tile> enemyUnits) {
        super(ultimateTarget, map, influenceMap, myUnits, enemyUnits);
    }

    @Override
    protected Mode determineMode() {
        return Mode.DEFEND;
    }
}
