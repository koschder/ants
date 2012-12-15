package tactics.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class DefaultCombatPositioning implements CombatPositioning {
    protected SearchableUnitMap map;
    protected InfluenceMap influenceMap;
    protected List<Tile> myUnits;
    protected List<Tile> enemyUnits;
    protected Tile target;
    protected Map<Tile, Tile> nextMoves = new HashMap<Tile, Tile>();

    protected enum Mode {
        DEFAULT,
        DEFEND,
        ATTACK,
        CONTROL, //
        FLEE;
    }

    public DefaultCombatPositioning(SearchableUnitMap map, InfluenceMap influenceMap, List<Unit> myUnits,
            List<Unit> enemyUnits, Tile ultimateTarget) {
        this.map = map;
        this.influenceMap = influenceMap;
        this.myUnits = getTiles(myUnits);
        this.enemyUnits = getTiles(enemyUnits);
        this.target = ultimateTarget;
        calculatePositions();
    }

    private void calculatePositions() {
        Mode mode = determineMode();
        switch (mode) {
        case FLEE:
            flee();
            break;
        case DEFEND:
            defendTarget();
            break;
        case ATTACK:
            attackTarget();
        case CONTROL:
            controlTarget();
        case DEFAULT:
        default:
            attackEnemy();
            break;
        }
    }

    private void attackTarget() {
        // TODO Auto-generated method stub

    }

    private void defendTarget() {
        // TODO Auto-generated method stub

    }

    private void controlTarget() {

    }

    protected Mode determineMode() {
        final boolean enemyIsSuperior = enemyUnits.size() > myUnits.size();
        if (enemyIsSuperior)
            return Mode.FLEE;
        return Mode.DEFAULT;
    }

    private void attackEnemy() {
        Tile clusterCenter = map.getClusterCenter(myUnits);
        Tile enemyClusterCenter = map.getClusterCenter(enemyUnits);
        List<Tile> formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, 0);
        if (getContainedFraction(formationTiles, myUnits) > 0.5) {
            // move forward
            formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, 2);
        }
        // perform positioning
        Set<Tile> targetedTiles = new HashSet<Tile>();
        sortByDistance(clusterCenter, myUnits);
        sortByDistance(clusterCenter, formationTiles);
        for (Tile uTile : myUnits) {
            for (Tile fTile : formationTiles) {
                if (targetedTiles.contains(fTile))
                    continue;
                moveToward(fTile, uTile);
                break;
            }
        }
    }

    private void flee() {
        for (Tile myUnit : myUnits) {
            nextMoves.put(myUnit, map.getSafestNeighbour(myUnit, influenceMap));
        }
    }

    private void moveToward(Tile targetTile, Tile myTile) {
        for (Aim aim : map.getDirections(myTile, targetTile)) {
            Tile nextTile = map.getTile(myTile, aim);
            if (!nextMoves.containsValue(nextTile)) {
                nextMoves.put(myTile, nextTile);
                break;
            }
        }
    }

    private void sortByDistance(final Tile center, List<Tile> tiles) {
        Collections.sort(tiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile t1, Tile t2) {
                return map.getSquaredDistance(t1, center) - map.getSquaredDistance(t2, center);
            }
        });
    }

    private List<Tile> getFormationTiles(final Tile clusterCenter, final Tile enemyClusterCenter, int stepForward) {
        final int dist = map.getSquaredDistance(clusterCenter, enemyClusterCenter) - stepForward;
        final int minDist = dist - 2;
        final int maxDist = dist + 2;
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        final List<Tile> formationTiles = bfs.floodFill(clusterCenter, dist, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                final int distance = map.getSquaredDistance(tile, enemyClusterCenter);
                return distance >= minDist && distance <= maxDist;
            }
        });
        if (stepForward == 0)
            formationTiles.add(clusterCenter);
        return formationTiles;
    }

    private float getContainedFraction(List<Tile> containing, List<Tile> candidates) {
        int contained = 0;
        for (Tile candidate : candidates) {
            if (containing.contains(candidate))
                contained++;
        }
        return contained / candidates.size();
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
        final Tile nextTile = nextMoves.get(u.getTile());
        return nextTile == null ? u.getTile() : nextTile;
    }
}
