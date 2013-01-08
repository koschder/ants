package tactics.combat;

import java.util.List;

import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.strategy.SearchableUnitMap;

/**
 * {@link CombatPositioning} implementation to be used when attacking a specific target.
 * 
 * @author kases1, kustl1
 * 
 */
public class AttackingCombatPositioning extends DefaultCombatPositioning {

    /**
     * Default constructor
     * 
     * @param ultimateTarget
     * @param map
     * @param influenceMap
     * @param myUnits
     * @param enemyUnits
     */
    public AttackingCombatPositioning(Tile ultimateTarget, SearchableUnitMap map, InfluenceMap influenceMap,
            List<Unit> myUnits, List<Tile> enemyUnits) {
        super(ultimateTarget, map, influenceMap, myUnits, enemyUnits);
    }

    /**
     * Constructor for convenient testing
     * 
     * @param map
     * @param influenceMap
     * @param myUnits
     * @param enemyUnits
     * @param ultimateTarget
     */
    public AttackingCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        super(map, influenceMap, myUnits, enemyUnits, ultimateTarget);
    }

    @Override
    protected Mode determineMode() {
        Tile clusterCenter = map.getClusterCenter(myUnits);
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        int distanceToTarget = map.getSquaredDistance(clusterCenter, target);
        List<Tile> enemiesGuardingTarget = bfs.floodFill(target, distanceToTarget, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return enemyUnits.contains(tile);
            }
        });
        if ((enemiesGuardingTarget.size()) <= myUnits.size())
            return Mode.ATTACK;

        // fall back to default
        return super.determineMode();
    }
}
