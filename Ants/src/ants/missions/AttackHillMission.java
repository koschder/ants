package ants.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.GoalTest;
import tactics.combat.AttackingCombatPositioning;
import tactics.combat.CombatPositioning;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Ilk;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;

/***
 * This mission used for attacking the enemies hills
 * 
 * @author kases1,kustl1
 * 
 */
public class AttackHillMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.ATTACK_HILLS);

    /***
     * the enemy hill.
     */
    Tile enemyHill;
    int attackSaftey = 35;
    int staySaftey = 75;
    int gatherAntsRadius = 30;
    State missionState = State.AttackEnemyHill;

    enum State {
        AttackEnemyHill,
        ControlEnemyHill,
        DestroyHill
    };

    public AttackHillMission(Tile hill) {
        enemyHill = hill;
        LOGGER.info("AttackHillMission created. EnemyHill is %s", enemyHill);
    }

    /***
     * mission is as long valid, as long the enemy hill exists.
     */
    @Override
    protected String isSpecificMissionValid() {
        if (!Ants.getWorld().getEnemyHills().contains(enemyHill))
            return "Enemy hill " + enemyHill + " is no longer there";
        return null;
    }

    private boolean otherEnemyHillNear(Ant a) {
        for (Mission m : Ants.getOrders().getMissions()) {
            if (!(m instanceof AttackHillMission))
                continue;
            AttackHillMission ahm = (AttackHillMission) m;

            if (ahm.isControlled() || ahm.equals(this))
                continue;

            if (Ants.getWorld().manhattanDistance(ahm.getHill(), a.getTile()) < 8) {
                if (Ants.getPathFinder().search(Strategy.AStar, a.getTile(), ahm.getHill(), 10) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isControlled() {
        return missionState == State.ControlEnemyHill;
    }

    @Override
    public Type getTaskType() {
        return Type.ATTACK_HILLS;
    }

    public Tile getHill() {
        return enemyHill;
    }

    @Override
    protected boolean checkAnts() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return !Ants.getWorld().getEnemyHills().contains(enemyHill);
    }

    @Override
    public void execute() {
        determineState();
        LOGGER.info("AttackHillMission: determineState state is %s for hill %s", missionState, enemyHill);
        releaseAnts();
        gatherAnts();
        moveAnts();

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "AttackHillMission attackers are: %s", ants);
    }

    private void releaseAnts() {
        List<Ant> antsToRelease = new ArrayList<Ant>();

        if (missionState == State.ControlEnemyHill) {

            List<Ant> controlAnts = getAntsAroundHill();
            if (controlAnts.size() >= 2) {
                controlAnts = controlAnts.subList(0, 2);

                for (Ant a : ants) {
                    if (!controlAnts.contains(a))
                        antsToRelease.add(a);
                }
            }
        } else {
            for (Ant a : ants) {
                if (otherEnemyHillNear(a))
                    antsToRelease.add(a);
            }
        }
        removeAnts(antsToRelease);
    }

    private List<Ant> getAntsAroundHill() {
        List<Ant> controlAnts = new ArrayList<Ant>();

        List<Tile> tiles = Ants.getWorld().get8Neighbours(enemyHill);

        for (Ant a : ants) {
            if (tiles.contains(a.getTile()))
                controlAnts.add(a);
        }
        return controlAnts;
    }

    private void determineState() {

        // if game is ending destroy all controlled hills
        if (Ants.getAnts().getTurns() - Ants.getAnts().getTurn() < 5) {
            missionState = State.DestroyHill;
            return;
        }

        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        final int searchDistance = Math.round(Ants.getWorld().getViewRadius2() / 2);
        List<Tile> friends = bfs.findFriendsInRadius(enemyHill, searchDistance);

        // if no one is close to control the hill, continue with the attack
        if (friends.isEmpty()) {
            missionState = State.AttackEnemyHill;
            return;
        }
        List<Tile> enemies = bfs.findEnemiesInRadius(enemyHill, searchDistance);

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "AttackHillMission enemies are: %s", enemies);
        LOGGER.info("AttackHillMission: determineState for hill %s: friends=%s enemies=%s", enemyHill, friends.size(),
                enemies.size());
        if (enemies.size() < 2 && friends.size() > 1) {
            missionState = State.ControlEnemyHill;
            Ants.getOrders().issueOrder(new Ant(enemyHill, 0), null, "AttackHillMission");
            return;
        } else if (enemies.size() >= friends.size()) {
            missionState = State.DestroyHill;
            return;
        }
        missionState = State.AttackEnemyHill;
    }

    private void gatherAnts() {
        if (missionState == State.AttackEnemyHill) {
            Map<Ant, List<Tile>> newAnts = gatherAnts(enemyHill, 5, gatherAntsRadius);
            LOGGER.info("AttackHillMission:new ants gathered for hill %s amount %s Ants: %s", enemyHill,
                    newAnts.size(), newAnts.keySet());

            for (Entry<Ant, List<Tile>> entry : newAnts.entrySet()) {
                Ant a = entry.getKey();
                a.setPath(entry.getValue());
                addAnt(a);
            }
        } else {
            LOGGER.info("AttackHillMission: NO gathering Ants for EnemyHill %s", enemyHill);
        }
    }

    private void moveAnts() {

        List<ArrayList<Unit>> groupedAnts = groupAnts(4, 10);
        for (List<Unit> group : groupedAnts) {
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, group.get(0).getTile(), enemyHill);

            Tile mileStone = path.size() <= 10 ? path.get(path.size() - 1) : path.get(10);

            List<Tile> enemy = getEnemiesInTheWay(Ants.getWorld().getClusterCenter(getTile(group)), mileStone);
            CombatPositioning pos = new AttackingCombatPositioning(mileStone, Ants.getWorld(), Ants.getInfluenceMap(),
                    group, enemy);
            for (Unit ant : group) {
                putMissionOrder((Ant) ant, pos.getNextTile(ant));
            }
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill,
                    "AttackingCombatPositioning for EnemyHill %s (mileStone: %s) " + pos.getLog(), enemyHill, mileStone);
        }
    }

    private List<Tile> getTile(List<Unit> group) {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Unit u : group)
            tiles.add(u.getTile());
        return tiles;
    }

    private List<Tile> getEnemiesInTheWay(final Tile clusterCenter, Tile target) {
        final int distanceToTarget = Ants.getWorld().getSquaredDistance(clusterCenter, target);
        BreadthFirstSearch bfs = new BreadthFirstSearch(Ants.getWorld());
        List<Tile> enemiesInTheWay = bfs.floodFill(target, distanceToTarget, new GoalTest() {

            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile) == Ilk.ENEMY_ANT
                        && Ants.getWorld().getSquaredDistance(clusterCenter, tile) < distanceToTarget;
            }
        });
        return enemiesInTheWay;
    }

    private List<ArrayList<Unit>> groupAnts(int radius, int maxDistance2) {
        List<ArrayList<Unit>> groupedAnts = new ArrayList<ArrayList<Unit>>();
        List<Unit> antAllocated = new ArrayList<Unit>();
        for (Ant a : ants) {
            if (antAllocated.contains(a))
                continue;
            LOGGER.debug("Calculate Near ants for: %s", a);
            ArrayList<Unit> nearAnts = getNearAnts(a, radius, maxDistance2);
            for (Unit allocated : antAllocated) {
                if (nearAnts.contains(allocated)) {
                    nearAnts.remove(allocated);
                }
            }
            groupedAnts.add(nearAnts);
            antAllocated.addAll(nearAnts);
        }
        return groupedAnts;
    }

    private ArrayList<Unit> getNearAnts(Ant baseAnt, int radius, int maxDistance2) {
        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        ArrayList<Unit> nearAnts = new ArrayList<Unit>();
        nearAnts.add(baseAnt);
        PriorityQueue<Tile> frontier = new PriorityQueue<Tile>();
        frontier.add(baseAnt.getTile());
        while (frontier.size() > 0) {
            LOGGER.debug("frontier is: %s nearAnts are: %s", frontier, nearAnts);
            Tile a = frontier.remove();
            List<Tile> tiles = bfs.findFriendsInRadius(a, radius * radius);
            for (Tile t : tiles) {
                if (frontier.contains(t) || Ants.getWorld().manhattanDistance(t, baseAnt.getTile()) > maxDistance2)
                    continue;
                Ant temp = new Ant(t, 0);
                if (!ants.contains(temp) || nearAnts.contains(temp))
                    continue;

                nearAnts.add(ants.get(ants.indexOf(temp)));
                frontier.add(a);
            }
        }
        return nearAnts;
    }

    private void moveAnts2() {
        List<Ant> antsToRelease = new ArrayList<Ant>();
        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        List<Tile> closeAnts = bfs.findFriendsInRadius(enemyHill, Ants.getWorld().getViewRadius2() * 2);
        List<Tile> enemies = bfs.findEnemiesInRadius(enemyHill, Ants.getWorld().getViewRadius2() * 2);
        List<Unit> combatAnts = new ArrayList<Unit>();
        List<Ant> nonCombatAnts = new ArrayList<Ant>();
        LOGGER.debug("closeAnts: %s", closeAnts);
        LOGGER.debug("enemies: %s", enemies);
        for (Ant a : ants) {
            if (closeAnts.contains(a.getTile()))
                combatAnts.add(a);
            else
                nonCombatAnts.add(a);
        }
        LOGGER.debug("CombatAnts: %s, NonCombatAnts: %s", combatAnts, nonCombatAnts);
        if (combatAnts.size() > 0) {
            CombatPositioning pos = new AttackingCombatPositioning(enemyHill, Ants.getWorld(), Ants.getInfluenceMap(),
                    combatAnts, enemies);
            for (Unit ant : combatAnts) {
                putMissionOrder((Ant) ant, pos.getNextTile(ant));
            }
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "AttackingCombatPositioning for %s " + pos.getLog(),
                    enemyHill);
        }
        for (Ant a : nonCombatAnts) {
            LOGGER.info("Move ant %s with path %s enemyhill %s", a, a.getPath(), enemyHill);
            String abortReason = checkEnviroment(a, false, false, false);
            if (abortReason.length() > 0) {
                // if food is nearby we get the food first.
                LOGGER.info("remove ant %s something interesting [%s] nearby", a, abortReason);
                antsToRelease.add(a);
            } else {
                if (!move(a)) {
                    antsToRelease.add(a);
                }
            }
        }
        LOGGER.info("Release ants %s of AttackHillMission %s", antsToRelease, enemyHill);
        removeAnts(antsToRelease);
    }

    private boolean move(Ant ant) {
        if (missionState == State.DestroyHill) {
            if (!recalculatePath(ant)) {
                LOGGER.info("Ant has a invalid path, and cannot be recalculated", ant);
                return false;
            }
            return moveToNextTileOnPath(ant);
        }
        if (missionState == State.ControlEnemyHill) {
            List<Aim> directions = Ants.getWorld().getDirections(ant.getTile(), enemyHill);
            if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 2 && directions.size() == 2) {
                LOGGER.info("Ant %s is controlling ControlEnemyHill, keep position.", ant);
                putMissionOrder(ant);
                return true;
            } else if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 1) {
                if (moveToSide(ant, directions.get(0)))
                    return true;
            }
        }

        if (!recalculatePath(ant)) {
            LOGGER.info("Ant has a invalid path, and cannot be recalculated", ant);
            return false;
        }
        if (!hasAntsOnAttackLine(ant) && hasAntBehind(ant)) {
            if (moveToSide(ant, ant.currentDirection())) {
                return true;
            }

        } else if (!hasAntsOnAttackLine(ant) && hasFollowingAnts(ant)) {
            LOGGER.info("Ant %s is wating on other ants witch are following behind.", ant);
            putMissionOrder(ant);
            return true;
        }

        Tile nextStep = ant.getPath().get(0);
        int safetyNextTile = (Ants.getInfluenceMap().getSafety(nextStep));
        boolean safeToMoveForward = safetyNextTile > attackSaftey || missionState != State.AttackEnemyHill;
        if (safeToMoveForward) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "next tile safety %s", safetyNextTile);
            LOGGER.info("AttackHillMission: ant %s trying forward move to EnemyHill is %s (safety: %s)", ant,
                    enemyHill, safetyNextTile);
            if (putMissionOrder(ant, nextStep)) {
                LOGGER.info("AttackHillMission: ant %s performing forward move to EnemyHill is %s (safety: %s)", ant,
                        enemyHill, safetyNextTile);
                ant.getPath().remove(0);
            } else {
                if (blockedByAntInMission(ant))
                    LOGGER.info("AttackHillMission: %s is blocked by ant in mission. try to calcuate offsetpath", ant);
                if (moveToOffsetPath(ant)) {
                    LOGGER.info("AttackHillMission: offsetpath calculated sucessful for ant %s", ant);
                    return true;
                }
                putMissionOrder(ant);
            }
            return true;
        } else {
            LOGGER.info("AttackHillMission: ant %s NO forward move to EnemyHill is %s (safety: %s)", ant, enemyHill,
                    safetyNextTile);
        }
        int currentSafety = (Ants.getInfluenceMap().getSafety(ant.getTile()));
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), ant.getTile(), "current safety %s next tile safety %s",
                currentSafety, safetyNextTile);
        if (currentSafety > staySaftey) {
            // current position is safety enough
            LOGGER.info(
                    "AttackHillMission:current position is safety enough (%s) safteyNextTile (%s), keep ant %s on position. Turns waited %s",
                    currentSafety, safetyNextTile, ant, ant.getTurnsWaited());
            putMissionOrder(ant);
        } else {
            moveToSafestTile(ant);
        }
        return true;
    }

    private void moveToSafestTile(Ant ant) {
        Tile safestTile = Ants.getWorld().getSafestNeighbour(ant.getTile(), Ants.getInfluenceMap());
        if (safestTile != null) {
            if (putMissionOrder(ant, safestTile)) {
                ant.getPath().add(0, ant.getTile());
                LOGGER.info("AttackHillMission:dangerous here! move ant %s backwards to %s. enemyHill %s", ant,
                        safestTile, enemyHill);
            }
        } else {
            putMissionOrder(ant);
        }
    }

    private boolean moveToSide(Ant ant, Aim aim) {
        List<Aim> aims = Aim.getOrthogonalAims(aim);
        for (Aim a : aims) {
            if (putMissionOrder(ant, a)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAntBehind(Ant ant) {
        Tile tile = ant.getPath().get(0);
        Aim currentAim = Ants.getWorld().getDirections(ant.getTile(), tile).get(0);
        Aim behind = Aim.getOpposite(currentAim);

        return ants.contains(Ants.getWorld().getTile(ant.getTile(), behind));
    }

    private boolean hasAntsOnAttackLine(Ant ant) {
        for (Aim a : Aim.getOrthogonalAims(ant.currentDirection())) {
            if (ants.contains(Ants.getWorld().getTile(ant.getTile(), a))) {
                LOGGER.info("there is an ant on the attackline of %s", ant);
                return true;
            }
        }
        return false;
    }

    private boolean hasFollowingAnts(Ant ant) {
        Tile tile = ant.getPath().get(0);
        Aim currentAim = Ants.getWorld().getDirections(ant.getTile(), tile).get(0);
        Aim opposite = Aim.getOpposite(currentAim);
        Tile back = Ants.getWorld().getTile(ant.getTile(), opposite);

        if (ants.contains(back))
            return true;

        for (Aim a : Aim.getOrthogonalAims(currentAim)) {
            if (ants.contains(Ants.getWorld().getTile(back, a)))
                return true;
        }
        return false;
    }

    private boolean moveToOffsetPath(Ant a) {
        Tile tile = a.getPath().get(0);
        Aim currentAim = Ants.getWorld().getDirections(a.getTile(), tile).get(0);
        List<Aim> aims = Aim.getOrthogonalAims(currentAim);

        for (Aim aim : aims) {
            Tile sideMove = Ants.getWorld().getTile(tile, aim);

            if (Ants.getWorld().isPassable(sideMove)) {
                if (Ants.getWorld().isPassable(Ants.getWorld().getTile(sideMove, currentAim))) {

                    if (putMissionOrder(a, aim)) {
                        // relcalculate path with a new offset.
                        List<Tile> newpath = generateOffsetpath(a.getPath(), aim);
                        // lead offset path to the enemy hill back
                        newpath.add(Ants.getWorld().getTile(newpath.get(newpath.size() - 1), Aim.getOpposite(aim)));
                        a.setPath(newpath);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Tile> generateOffsetpath(List<Tile> path, Aim aim) {
        List<Tile> newPath = new ArrayList<Tile>();
        for (Tile t : path) {
            newPath.add(Ants.getWorld().getTile(t, aim));
        }
        return newPath;
    }

    private boolean blockedByAntInMission(Ant a) {
        Tile tile = a.getPath().get(0);
        return (ants.contains(tile));
    }

    /***
     * if the fift
     * 
     * @param ant
     * @return
     */
    private boolean recalculatePath(Ant ant) {
        int tileToCheck = Math.min(5, ant.getPath().size() - 1);
        // check if path doesn't lead throw water
        boolean pathThroughWater = ant.hasPath() ? !Ants.getWorld().isPassable(ant.getPath().get(tileToCheck)) : false;
        boolean nextPathStepValid = ant.hasPath() ? (Ants.getWorld().manhattanDistance(ant.getTile(),
                ant.getPath().get(0)) == 1) : false;

        if (!nextPathStepValid || pathThroughWater) {
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, ant.getTile(), enemyHill, 30);
            if (path == null || path.size() == 0) {
                return false;
            }
            LOGGER.info("AttackHillMission:recalculatePath path for ant %s path: %s.", ant, path);
            ant.setPath(path);
            return true;
        }
        return true;
    }
}
