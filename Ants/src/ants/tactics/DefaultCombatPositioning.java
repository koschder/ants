package ants.tactics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.entities.Aim;
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
        final boolean enemyIsSuperior = enemyUnits.size() > myUnits.size();
        final boolean weAreSuperior = enemyUnits.size() < myUnits.size();
        if (enemyIsSuperior) {
            for (Unit myUnit : myUnits) {
                nextMoves.put(myUnit, map.getSafestNeighbour(myUnit.getTile(), influenceMap));
            }
        } else if (weAreSuperior) {
            Tile clusterCenter = map.getClusterCenter(getTiles(myUnits));
            for (Unit myUnit : myUnits) {
                for (Aim aim : map.getDirections(myUnit.getTile(), clusterCenter)) {
                    Tile nextTile = map.getTile(myUnit.getTile(), aim);
                    if (!nextMoves.containsValue(nextTile)) {
                        nextMoves.put(myUnit, nextTile);
                        break;
                    }
                }
            }
        }
    }

    private List<Tile> getTiles(List<Unit> units) {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Unit unit : units) {
            tiles.add(unit.getTile());
        }
        return tiles;
    }

    @Override
    public Tile getNextTile(Unit u) {
        final Tile nextTile = nextMoves.get(u);
        return nextTile == null ? u.getTile() : nextTile;
    }
}
