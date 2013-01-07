package tactics.combat;

import java.util.List;

import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.strategy.SearchableUnitMap;

/**
 * {@link CombatPositioning} implementation to be used when defending a specific target.
 * 
 * @author kases1, kustl1
 * 
 */
public class DefendingCombatPositioning extends DefaultCombatPositioning {
    /**
     * Constructor for convenient testing
     * 
     * @param map
     * @param influenceMap
     * @param myUnits
     * @param enemyUnits
     * @param ultimateTarget
     */
    public DefendingCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        super(map, influenceMap, myUnits, enemyUnits, ultimateTarget);
    }

    /**
     * Default constructor
     * 
     * @param ultimateTarget
     * @param map
     * @param influenceMap
     * @param myUnits
     * @param enemyUnits
     */
    public DefendingCombatPositioning(Tile ultimateTarget, SearchableUnitMap map, InfluenceMap influenceMap,
            List<Unit> myUnits, List<Tile> enemyUnits) {
        super(ultimateTarget, map, influenceMap, myUnits, enemyUnits);
    }

    @Override
    protected Mode determineMode() {
        return Mode.DEFEND;
    }

    @Override
    protected boolean shouldStepOnTarget() {
        return false;
    }
}
