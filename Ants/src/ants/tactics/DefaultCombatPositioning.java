package ants.tactics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.entities.Tile;
import api.entities.Unit;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class DefaultCombatPositioning implements CombatPositioning {
    private SearchableUnitMap map;
    private InfluenceMap influenceMap;
    private List<Unit> myUnits;
    private List<Unit> enemyUnits;
    private Tile target;
    private Map<Unit, Tile> nextMoves = new HashMap<Unit, Tile>();

    public DefaultCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        this.map = map;
        this.influenceMap = influenceMap;
        this.myUnits = myUnits;
        this.enemyUnits = enemyUnits;
        this.target = ultimateTarget;
        calculatePositions();
    }

    private void calculatePositions() {
        final boolean isEnemySuperior = enemyUnits.size() > myUnits.size();
        if (isEnemySuperior && myUnits.size() <= 1) {
            for (Unit myUnit : myUnits) {
                nextMoves.put(myUnit, map.getSafestNeighbour(myUnit.getTile(), influenceMap));
            }
        }
    }

    @Override
    public Tile getNextTile(Unit u) {
        final Tile nextTile = nextMoves.get(u);
        return nextTile == null ? u.getTile() : nextTile;
    }
}
