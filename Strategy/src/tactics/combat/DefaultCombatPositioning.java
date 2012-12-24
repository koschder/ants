package tactics.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

public class DefaultCombatPositioning implements CombatPositioning {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.COMBAT_POSITIONING);
    protected SearchableUnitMap map;
    protected InfluenceMap influenceMap;
    protected List<Tile> myUnits;
    protected List<Tile> enemyUnits;
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
        final Tile clusterCenter = map.getClusterCenter(myUnits);
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
            final Tile enemyClusterCenter = map.getClusterCenter(enemiesInTheWay);
            attackEnemy(clusterCenter, enemyClusterCenter);
        }
        LOGGER.debug(log);
        this.log += log;
    }

    public String getLog() {
        return this.log;
    }

    private void defendTarget() {
        // if no opponents are around, just position ourselves in the diagonals
        if (enemyUnits.isEmpty()) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(map);
            List<Tile> formationTiles = bfs.getDiagonalNeighbours(target);
            // TODO choose a specific "center", e.g. one opposite water
            positionUnits(formationTiles.get(0), formationTiles);

        } else {
            // group enemies to determine which side of the hill needs how much protection

            // position ants between enemy and hill
            Tile enemyClusterCenter = map.getClusterCenter(enemyUnits);
            Tile clusterCenter = calculateDefenseClusterCenter(enemyClusterCenter);
            List<Tile> formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), false);
            formationTiles.add(clusterCenter);
            positionUnits(clusterCenter, formationTiles);
            // if an enemy is isolated, try to take him down to free up resources, but don't run away too far
        }

    }

    private Tile calculateDefenseClusterCenter(Tile enemyClusterCenter) {
        // TODO the cluster center should move toward the enemy if there are many defenders, so we don't block paths
        // away from the hill
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
        Tile clusterCenter = map.getClusterCenter(myUnits);
        Tile enemyClusterCenter;
        // TODO this should really be a separate mode, so we can actually do something sensible
        // TODO what if getSafestNeighbour returns null?
        if (enemyUnits.isEmpty())
            enemyClusterCenter = map.getSafestNeighbour(myUnits.get(myUnits.size() - 1), influenceMap);
        else
            enemyClusterCenter = map.getClusterCenter(enemyUnits);
        attackEnemy(clusterCenter, enemyClusterCenter);
    }

    private void attackEnemy(Tile clusterCenter, Tile enemyClusterCenter) {
        String log;
        float limit = 0.5f;
        log = "AttackEnemy: clusterCenter:" + clusterCenter;
        log += ", enemyClusterCenter:" + enemyClusterCenter;

        List<Tile> formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), false);
        formationTiles.add(clusterCenter);
        float percentFormated = getContainedFraction(formationTiles, myUnits);
        log += ", pf: " + percentFormated + " (lim: " + limit + ")";
        if (percentFormated > limit) {
            // move forward
            log += "=> forward ants";
            formationTiles = getFormationTiles(clusterCenter, enemyClusterCenter, myUnits.size(), true);
        } else {
            log += "=> format ants";
        }
        log += ", formationTiles: " + formationTiles;
        // perform positioning
        positionUnits(clusterCenter, formationTiles);
        LOGGER.debug(log);
        this.log = log;
    }

    private void positionUnits(Tile clusterCenter, List<Tile> formationTiles) {
        Set<Tile> targetedTiles = new HashSet<Tile>();
        sortByDistance(clusterCenter, myUnits);
        sortByDistance(clusterCenter, formationTiles);
        for (Tile unit : myUnits) {
            for (Tile fTile : formationTiles) {
                if (targetedTiles.contains(fTile))
                    continue;
                Tile actualTile = moveToward(fTile, unit, clusterCenter, formationTiles);
                if (actualTile != null) {
                    targetedTiles.add(actualTile);
                    break;
                }
            }
        }
    }

    private void flee() {
        for (Tile myUnit : myUnits) {
            nextMoves.put(myUnit, map.getSafestNeighbour(myUnit, influenceMap));
        }
    }

    private Tile moveToward(Tile targetTile, Tile myTile, Tile clusterCenter, List<Tile> formationTiles) {
        final List<Aim> directions = map.getDirections(myTile, targetTile);
        int shortestDistance = Integer.MAX_VALUE;
        Tile bestTile = null;
        for (Aim aim : directions) {
            Tile nextTile = map.getTile(myTile, aim);
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

    // private List<Tile> getFormationTiles(final Tile clusterCenter, final Tile enemyClusterCenter, boolean advance) {
    // int squaredDistance = map.getSquaredDistance(clusterCenter, enemyClusterCenter);
    //
    // int distance = (int) Math.sqrt(squaredDistance);
    // if (advance) {
    // distance += -1;
    // squaredDistance = (int) Math.pow(distance, 2);
    // }
    //
    // int stepForward = distance * 2;
    //
    // final int dist = squaredDistance;
    // final int minDist = dist - stepForward;
    // final int maxDist = dist + stepForward;
    // BreadthFirstSearch bfs = new BreadthFirstSearch(map);
    // final List<Tile> formationTiles = bfs.floodFill(clusterCenter, maxDist, new GoalTest() {
    //
    // @Override
    // public boolean isGoal(Tile tile) {
    // final int distance = map.getSquaredDistance(tile, enemyClusterCenter);
    // return distance >= minDist && distance <= maxDist;
    // }
    // });
    // return formationTiles;
    // }

    private List<Tile> getFormationTiles(Tile clusterCenter, final Tile enemyClusterCenter, int ants,
            boolean moveForward) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(map);
        int squaredDistance = map.getSquaredDistance(clusterCenter, enemyClusterCenter);
        int distance = (int) Math.sqrt(squaredDistance);
        if (moveForward) {
            distance--;
        }

        ants += 1;// more opportiunities

        List<Tile> formationTiles = new ArrayList<Tile>();
        List<Tile> tempTiles;
        do {
            distance++;
            final int dist = (int) Math.pow(distance, 2);
            final int minDist = dist - distance * 2;
            final int maxDist = dist;
            final int maxSpread = (int) Math.pow(distance * 2 - 1.5, 2);
            tempTiles = bfs.findClosestTiles(clusterCenter, ants - formationTiles.size(), Integer.MAX_VALUE, maxSpread,
                    new GoalTest() {

                        @Override
                        public boolean isGoal(Tile tile) {
                            final int distance = map.getSquaredDistance(tile, enemyClusterCenter);
                            return distance >= minDist && distance <= maxDist;
                        }
                    });
            formationTiles.addAll(tempTiles);
            // as long as we need formation tiles and we get new.
        } while (tempTiles.size() > 0 && formationTiles.size() < ants);
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
}
