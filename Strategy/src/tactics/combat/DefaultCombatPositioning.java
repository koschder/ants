package tactics.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import pathfinder.PathFinder.Strategy;
import pathfinder.SimplePathFinder;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import strategy.LogCategory;
import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;
import api.strategy.InfluenceMap;
import api.strategy.SearchableUnitMap;

public class DefaultCombatPositioning implements CombatPositioning {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.COMBAT_POSITIONING);
    protected SearchableUnitMap map;
    protected InfluenceMap influenceMap;
    protected List<Tile> myUnits;
    protected List<Tile> enemyUnits;
    private List<Tile> formationTiles;
    private Tile enemyClusterCenter;
    private Tile clusterCenter;
    protected Tile target;
    protected String log = "";
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
        this(ultimateTarget, map, influenceMap, myUnits, getTiles(enemyUnits));
    }

    public DefaultCombatPositioning(Tile ultimateTarget, SearchableUnitMap map, InfluenceMap influenceMap,
            List<Unit> myUnits, List<Tile> enemyUnits) {
        this.map = map;
        this.influenceMap = influenceMap;
        this.myUnits = getTiles(myUnits);
        this.enemyUnits = enemyUnits;
        this.target = ultimateTarget;
        calculatePositions();
    }

    private void calculatePositions() {
        Mode mode = determineMode();
        log += "mode is: " + mode;
        log += "\n, myUnits: " + myUnits;
        log += "\n, enemyUnits: " + enemyUnits;
        switch (mode) {
        case FLEE:
            flee();
            break;
        case DEFEND:
            defendTarget();
            break;
        case ATTACK:
            attackTarget();
            break;
        case CONTROL:
            controlTarget();
            break;
        case DEFAULT:
        default:
            attackEnemy();
            break;
        }
        LOGGER.debug("Next moves: %s", nextMoves);
    }

    private void attackTarget() {
        String log;
        log = " AttackTarget: ";
        clusterCenter = map.getClusterCenter(myUnits);
        log += ", MyClusterCenter: " + clusterCenter;

        final int distanceToTarget = map.getSquaredDistance(clusterCenter, target);
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        List<Tile> enemiesInTheWay = bfs.floodFill(target, distanceToTarget, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return enemyUnits.contains(tile) && map.getSquaredDistance(clusterCenter, tile) < distanceToTarget;
            }
        });

        log += ", enemiesInTheWay: " + enemiesInTheWay;
        if (enemiesInTheWay.isEmpty()) {
            for (Tile uTile : myUnits) {
                PathFinder pf = new SimplePathFinder(map, influenceMap);
                List<Tile> path = pf.search(Strategy.AStar, uTile, target);
                if (path != null && path.size() > 1 && !nextMoves.containsValue(path.get(1))) {
                    nextMoves.put(uTile, path.get(1));
                } else
                    nextMoves.put(uTile, uTile);
            }
        } else {
            enemyClusterCenter = map.getClusterCenter(enemiesInTheWay);
            attackEnemy(clusterCenter, enemyClusterCenter);
        }
        LOGGER.debug(log);
        this.log += log;
    }

    public String getLog() {
        return this.log;
    }

    private void defendTarget() {
        log = " DefendTarget: ";
        // if no opponents are around, just position ourselves in the diagonals
        if (enemyUnits.isEmpty()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(map);
            List<Tile> formationTiles = bfs.getDiagonalNeighbours(target);
            positionUnits(formationTiles.get(0), formationTiles);
            log += ", FormationTiles: " + formationTiles;
        } else {
            // check for water
            List<Aim> sidesToProtect = getSidesToProtect(target);
            // group enemies to determine which side of the target needs how much protection
            // TODO implement k-means for proper clustering
            Map<Aim, List<Tile>> enemiesPerSide = new HashMap<Aim, List<Tile>>();
            for (Aim aim : sidesToProtect) {
                List<Tile> enemies = getUnitsInDirection(aim, enemyUnits, false);
                if (enemies.isEmpty())
                    continue;
                enemiesPerSide.put(aim, enemies);
            }

            Map<Aim, List<Tile>> defendersPerSide = getDefendersPerSide(enemiesPerSide);
            // position units between enemy and target
            for (Entry<Aim, List<Tile>> entry : enemiesPerSide.entrySet()) {
                Tile enemyClusterCenter = map.getClusterCenter(entry.getValue());
                Tile clusterCenter = calculateDefenseClusterCenter(enemyClusterCenter);
                List<Tile> formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), false);
                formationTiles.add(clusterCenter);
                positionUnits(defendersPerSide.get(entry.getKey()), clusterCenter, formationTiles);

                log += ", MyClusterCenter(" + entry.getKey() + "): " + clusterCenter;
                log += ", EnemyClusterCenter(" + entry.getKey() + "): " + enemyClusterCenter;
                log += ", FormationTiles(" + entry.getKey() + "): " + formationTiles;
                log += ", Defenders(" + entry.getKey() + "): " + defendersPerSide.get(entry.getKey());
                log += "<br/><br/>";
            }

            // if an enemy is isolated, try to take him down to free up resources, but don't run away too far
        }

    }

    private List<Tile> getUnitsInDirection(Aim direction, List<Tile> units, boolean onlyAssignOnce) {
        List<Tile> unitsInDirection = new ArrayList<Tile>();
        for (Tile unit : units) {
            if (onlyAssignOnce && map.getPrincipalDirection(target, unit) == direction)
                unitsInDirection.add(unit);
            else if (map.getDirections(target, unit).contains(direction))
                unitsInDirection.add(unit);
        }
        return unitsInDirection;
    }

    private Map<Aim, List<Tile>> getDefendersPerSide(Map<Aim, List<Tile>> enemiesPerSide) {
        Map<Aim, List<Tile>> defendersPerSide = new HashMap<Aim, List<Tile>>();
        // TODO this means an enemy is attacking from a supposedly protected side. Ideally we should catch that, but in
        // practice it occurs very rarely
        if (enemiesPerSide.isEmpty())
            return defendersPerSide;
        List<Tile> assignedDefenders = new ArrayList<Tile>();
        for (Aim aim : enemiesPerSide.keySet()) {
            List<Tile> defenders = getUnitsInDirection(aim, myUnits, true);
            defendersPerSide.put(aim, defenders);
            assignedDefenders.addAll(defenders);
        }
        TreeMap<Aim, List<Tile>> sortedEnemies = new TreeMap<Aim, List<Tile>>(new ListSizeComparator(enemiesPerSide));
        sortedEnemies.putAll(enemiesPerSide);

        Aim mostAttackers = sortedEnemies.firstKey();
        for (Tile tile : myUnits) {
            if (assignedDefenders.contains(tile))
                continue;
            defendersPerSide.get(mostAttackers).add(tile);
            assignedDefenders.add(tile);
        }
        return defendersPerSide;
    }

    class ListSizeComparator implements Comparator<Aim> {

        Map<Aim, List<Tile>> base;

        public ListSizeComparator(Map<Aim, List<Tile>> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(Aim a, Aim b) {
            if (base.get(a).size() >= base.get(b).size()) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    private List<Aim> getSidesToProtect(Tile target) {
        Map<Aim, Integer> distanceToWater = new HashMap<Aim, Integer>();
        for (Aim aim : Aim.values()) {
            distanceToWater.put(aim, map.getManhattanDistanceToNextImpassableTile(target, aim));
        }
        List<Aim> sidesToProtect = new ArrayList<Aim>();
        for (Entry<Aim, Integer> entry : distanceToWater.entrySet()) {
            if (entry.getValue() < 5)
                continue; // protected by water
            sidesToProtect.add(entry.getKey());
        }
        return sidesToProtect;
    }

    private Tile calculateDefenseClusterCenter(Tile enemyClusterCenter) {
        // TODO the cluster center should move toward the enemy if there are many defenders, so we don't block paths
        // away from the target
        Aim direction = map.getPrincipalDirection(target, enemyClusterCenter);
        return map.getTile(target, direction);
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
        clusterCenter = map.getClusterCenter(myUnits);
        // TODO this should really be a separate mode, so we can actually do something sensible
        final Tile furthestUnitTile = myUnits.get(myUnits.size() - 1);
        if (enemyUnits.isEmpty())
            enemyClusterCenter = map.getSafestNeighbour(furthestUnitTile, influenceMap);
        else
            enemyClusterCenter = map.getClusterCenter(enemyUnits);
        attackEnemy(clusterCenter, enemyClusterCenter != null ? enemyClusterCenter : furthestUnitTile);
    }

    private void attackEnemy(Tile clusterCenter, Tile enemyClusterCenter) {
        String log;
        float limit = 0.5f;
        log = "AttackEnemy: clusterCenter:" + clusterCenter;
        log += ", enemyClusterCenter:" + enemyClusterCenter;

        formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), false);
        formationTiles.add(clusterCenter);
        float percentFormated = getContainedFraction(formationTiles, myUnits);
        log += ", pf: " + percentFormated + " (lim: " + limit + ")";
        if (percentFormated > limit) {
            // move forward
            log += "=> forward units";
            formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), true);
        } else {
            log += "=> format units";
        }
        log += ", formationTiles: " + formationTiles;
        // perform positioning
        positionUnits(enemyClusterCenter, formationTiles);
        LOGGER.debug(log);
        this.log = log;
    }

    private void positionUnits(List<Tile> units, Tile clusterCenter, List<Tile> formationTiles) {

        String log = "Positioning for " + units + " fromationTiles are " + formationTiles;

        Set<Tile> targetedTiles = new HashSet<Tile>();
        sortByDistance(clusterCenter, units);
        sortByDistance(clusterCenter, formationTiles);
        for (Tile unit : units) {
            for (Tile fTile : formationTiles) {
                if (targetedTiles.contains(fTile))
                    continue;
                Tile actualTile = moveToward(fTile, unit, clusterCenter, formationTiles);
                if (actualTile != null) {
                    targetedTiles.add(actualTile);
                    log += "Unit " + unit + " to " + actualTile;
                    break;
                }
            }
        }
        LOGGER.info(log);
    }

    private void positionUnits(Tile clusterCenter, List<Tile> formationTiles) {
        positionUnits(myUnits, clusterCenter, formationTiles);
    }

    private void flee() {
        for (Tile myUnit : myUnits) {
            nextMoves.put(myUnit, map.getSafestNeighbour(myUnit, influenceMap));
        }
    }

    protected boolean shouldStepOnTarget() {
        return true;
    }

    private Tile moveToward(Tile targetTile, Tile myTile, Tile clusterCenter, List<Tile> formationTiles) {
        final List<Aim> directions = map.getDirections(myTile, targetTile);
        int shortestDistance = Integer.MAX_VALUE;
        Tile bestTile = null;
        for (Aim aim : directions) {
            Tile nextTile = map.getTile(myTile, aim);
            // if a unit is already in position, don't assume it will move
            if (myUnits.contains(nextTile) && formationTiles.contains(nextTile))
                continue;
            if (!map.isPassable(nextTile))
                continue;
            if (nextMoves.containsValue(nextTile))
                continue;
            if (formationTiles.contains(nextTile)) {
                bestTile = nextTile;
                break;
            }
            int dist = map.manhattanDistance(nextTile, clusterCenter);
            if (dist < shortestDistance) {
                shortestDistance = dist;
                bestTile = nextTile;
            }
        }
        bestTile = bestTile == null ? myTile : bestTile;
        nextMoves.put(myTile, bestTile);
        return bestTile;
    }

    private void sortByDistance(final Tile center, List<Tile> tiles) {
        Collections.sort(tiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile t1, Tile t2) {
                return map.getSquaredDistance(t1, center) - map.getSquaredDistance(t2, center);
            }
        });
    }

    private List<Tile> getFormationTiles(Tile clusterCenter, final Tile enemyClusterCenter, int units,
            boolean moveForward) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        int squaredDistance = map.getSquaredDistance(clusterCenter, enemyClusterCenter);
        int distance = (int) Math.sqrt(squaredDistance);
        if (moveForward) {
            distance--;
        }

        List<Tile> formationTiles = new ArrayList<Tile>();
        distance++;
        final int dist = (int) Math.pow(distance, 2);
        final int minDist = dist - distance * 2;
        final int maxDist = dist;
        final int maxSpread = (int) Math.pow(distance * 2 - 1.5, 2);
        formationTiles = bfs.findClosestTiles(clusterCenter, units, Integer.MAX_VALUE, maxSpread, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                if (!shouldStepOnTarget() && tile.equals(target))
                    return false;
                final int distance = map.getSquaredDistance(tile, enemyClusterCenter);
                return distance >= minDist && distance <= maxDist;
            }
        });
        return formationTiles;
    }

    private float getContainedFraction(List<Tile> containing, List<Tile> candidates) {
        int contained = 0;
        for (Tile candidate : candidates) {
            if (containing.contains(candidate))
                contained++;
        }
        return ((float) contained) / ((float) candidates.size());
    }

    private static List<Tile> getTiles(List<Unit> units) {
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

    public List<Tile> getFormationTiles() {
        return formationTiles;
    }

    public Tile getEnemyClusterCenter() {
        return enemyClusterCenter;
    }

    public Tile getClusterCenter() {
        return clusterCenter;
    }
}
