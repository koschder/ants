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

            if (ahm.isControlled() || ahm.getHill().equals(getHill()))
                continue;

            if (Ants.getWorld().manhattanDistance(ahm.getHill(), a.getTile()) < 8) {
                if (Ants.getPathFinder().search(Strategy.AStar, a.getTile(), ahm.getHill(), 10) != null) {
                    LOGGER.info("AttackHillMission: release Ant %s for nearer EnemyHill %s", a, ahm.getHill());
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
        gatherAnts();
        releaseAnts();
        moveAnts();

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "AttackHillMission attackers are: %s state is %s", ants,
                missionState);
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
                String abortReason = checkEnviroment(a, true, false, false, true);
                if (otherEnemyHillNear(a) || abortReason.length() > 0)
                    antsToRelease.add(a);
                LOGGER.info("remove ant %s something interesting [%s] nearby", a,
                        abortReason.length() > 0 ? abortReason : "otherEnemyHill");
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

        // if last missionState was ControlEnemyHill we remove attacker on the hill because they
        // get eliminated so soon as they see daylight.
        if (missionState == State.ControlEnemyHill)
            enemies.remove(enemyHill);

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
        int amount = 0;
        if (missionState == State.AttackEnemyHill) {
            amount = 5;
        } else if (missionState == State.ControlEnemyHill) {
            amount = 2 - ants.size(); // we need two ants to control the hill
        }
        Map<Ant, List<Tile>> newAnts = gatherAnts(enemyHill, amount, gatherAntsRadius);
        LOGGER.info("AttackHillMission:new ants gathered for hill %s amount %s Ants: %s", enemyHill, newAnts.size(),
                newAnts.keySet());

        for (Entry<Ant, List<Tile>> entry : newAnts.entrySet()) {
            Ant a = entry.getKey();
            a.setPath(entry.getValue());
            addAnt(a);
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
        if (Ants.getWorld().getIlk(target) == Ilk.ENEMY_ANT) {
            enemiesInTheWay.add(target);
        }

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

    private void moveAnts() {
        if (missionState == State.AttackEnemyHill) {
            moveInCombatFormation();
        } else { // missionState is DestroyEnemyHill or ControlEnemyHill
            List<Ant> antsToRelease = new ArrayList<Ant>();
            for (Ant a : ants) {
                LOGGER.info("Move ant %s with path %s enemyhill %s", a, a.getPath(), enemyHill);
                if (!move(a)) {
                    antsToRelease.add(a);
                }
            }
            LOGGER.info("Release ants %s of AttackHillMission %s", antsToRelease, enemyHill);
            removeAnts(antsToRelease);
        }
    }

    private void moveInCombatFormation() {
        List<ArrayList<Unit>> groupedAnts = groupAnts(4, 10);
        for (List<Unit> group : groupedAnts) {
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, group.get(0).getTile(), enemyHill);

            Tile mileStone = path.size() <= 10 ? path.get(path.size() - 1) : path.get(10);

            List<Tile> enemy = getEnemiesInTheWay(Ants.getWorld().getClusterCenter(getTile(group)), mileStone);
            CombatPositioning pos = new AttackingCombatPositioning(mileStone, Ants.getWorld(), Ants.getInfluenceMap(),
                    group, enemy);
            for (Unit u : group) {
                Ant ant = (Ant) u;
                ant.setPath(null); // reset path, it will not be valid
                putMissionOrder(ant, pos.getNextTile(ant));
            }
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), enemyHill, "ACP: (mileStone: %s) " + pos.getLog(), mileStone);
        }
    }

    private boolean move(Ant ant) {
        if (missionState == State.DestroyHill) {
            if (!recalculatePath(ant)) {
                LOGGER.info("Ant %s has a invalid path, and cannot be recalculated", ant);
                return false;
            }
            return moveToNextTileOnPath(ant);
        } else if (missionState == State.ControlEnemyHill) {
            List<Aim> directions = Ants.getWorld().getDirections(ant.getTile(), enemyHill);
            if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 2 && directions.size() == 2) {
                LOGGER.info("Ant %s is controlling ControlEnemyHill, keep position.", ant);
                putMissionOrder(ant);
                return true;
            } else if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 1) {
                if (moveToSide(ant, directions.get(0)))
                    return true;
            }
            if (!recalculatePath(ant)) {
                LOGGER.info("Ant %s has a invalid path, and cannot be recalculated", ant);
                return false;
            }
            return moveToNextTileOnPath(ant);
        }
        throw new IllegalArgumentException("invalid missionState in move(Ant ant): " + missionState);
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

    /***
     * checks if the path is still valid and recalculated it, if necessary
     * 
     * @param ant
     * @return
     */
    private boolean recalculatePath(Ant ant) {

        boolean isValid = ant.hasPath();
        if (isValid) {
            // check if path doesn't lead throw water
            int nextTile = Math.min(5, ant.getPath().size() - 1);
            isValid = Ants.getWorld().isPassable(ant.getPath().get(nextTile));
        }
        if (isValid) {
            // check next step is one tile away
            LOGGER.error("path of  ant %s messed up: path: %s.", ant, ant.getPath());
            isValid = Ants.getWorld().manhattanDistance(ant.getTile(), ant.getPath().get(0)) == 1;
        }
        if (!isValid) { // recalculate if not valid
            List<Tile> path = Ants.getPathFinder().search(Strategy.AStar, ant.getTile(), enemyHill, 30);
            if (path == null || path.size() == 0) {
                return false;
            }
            LOGGER.info("AttackHillMission:recalculatePath path for ant %s path: %s.", ant, path);
            ant.setPath(path);
        }
        return true;
    }
}
