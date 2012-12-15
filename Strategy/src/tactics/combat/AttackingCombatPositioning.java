package tactics.combat;

import java.util.List;

import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import api.entities.Tile;
import api.entities.Unit;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class AttackingCombatPositioning extends DefaultCombatPositioning {

    public AttackingCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        super(map, influenceMap, myUnits, enemyUnits, ultimateTarget);
    }

    @Override
    protected Mode determineMode() {
        Tile clusterCenter = map.getClusterCenter(myUnits);
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        int distanceToTarget = map.getSquaredDistance(clusterCenter, target);
        List<Tile> enemiesGuardingTarget = bfs.floodFill(clusterCenter, distanceToTarget, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return enemyUnits.contains(tile);
            }
        });
        if ((enemiesGuardingTarget.size() * 2) < myUnits.size())
            return Mode.ATTACK;

        // TODO if no enemies are near & we have at least 2 units, return Mode.CONTROL

        // fall back to default
        return super.determineMode();
    }
}
