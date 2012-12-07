package ants.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
import api.entities.Aim;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

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
    int attackSaftey = 30;
    int staySaftey = -50;
    int gatherAntsRadius = 20;
    State missionState = State.AttackEnemyHill;

    enum State {
        AttackEnemyHill,
        ControlEnemyHill,
        DestoryHill
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

    private boolean otherEnemyHillNear(Tile hill, Ant a) {
        for (Mission m : Ants.getOrders().getMissions()) {
            if (!(m instanceof AttackHillMission))
                continue;
            AttackHillMission ahm = (AttackHillMission) m;

            if (ahm.isControlled() || ahm.equals(this))
                continue;

            if (Ants.getWorld().manhattanDistance(enemyHill, a.getTile()) < 8) {
                if (Ants.getPathFinder().search(Strategy.AStar, a.getTile(), enemyHill, 10) != null) {
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
                if (otherEnemyHillNear(enemyHill, a))
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
            missionState = State.AttackEnemyHill;
            return;
        }

        Ant a = getNearestAnt();
        if (a == null || a.getPath().size() > 5) {
            missionState = State.AttackEnemyHill;
            return;
        }
        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        List<Tile> enemy = bfs.findEnemiesInRadius(enemyHill, Ants.getWorld().getAttackRadius2());
        List<Tile> friend = bfs.findFriendsInRadius(enemyHill, Ants.getWorld().getAttackRadius2());
        LOGGER.info("AttackHillMission: determineState for hill %s friends %s enemy near Ant %s", friend.size(),
                enemy.size(), a);
        if (enemy.size() < 2 && friend.size() > 1) {
            missionState = State.ControlEnemyHill;
            Ants.getOrders().issueOrder(new Ant(enemyHill, 0), null, "DefendHillMission");
            return;
        }
        missionState = State.AttackEnemyHill;
    }

    private Ant getNearestAnt() {
        int min = 999;
        Ant ant = null;
        for (Ant a : ants) {
            if (a.hasPath() && a.getPath().size() < min) {
                ant = a;
                min = a.getPath().size();
            }
        }
        return ant;
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
        List<Ant> antsToRelease = new ArrayList<Ant>();
        for (Ant a : ants) {
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

        if (missionState == State.ControlEnemyHill) {
            if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 2
                    && Ants.getWorld().getDirections(ant.getTile(), enemyHill).size() == 2) {
                LOGGER.info("Ant %s is controlling ControlEnemyHill, keep position.", ant);
                putMissionOrder(ant);
                return true;
            } else if (Ants.getWorld().manhattanDistance(ant.getTile(), enemyHill) == 1) {
                if (moveToSide(ant))
                    return true;
            }
        }

        if (!recalculatePath(ant)) {
            LOGGER.info("Ant has a invalid path, and cannot be recalculated", ant);
            return false;
        }
        if (!hasAntsOnAttackLine(ant) && hasAntBehind(ant)) {
            // TODO adjust path?
            if (moveToSide(ant)) {
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

        if (currentSafety > staySaftey) {
            // current position is safety enough
            LOGGER.info(
                    "AttackHillMission:current position is safety enough (%s) safteyNextTile (%s), keep ant %s on position. Turns waited %s",
                    currentSafety, safetyNextTile, ant, ant.getTurnsWaited());
            putMissionOrder(ant);
        } else {
            moveToSafetiestTile(ant);
        }
        return true;
    }

    private void moveToSafetiestTile(Ant ant) {
        List<SearchTarget> list = Ants.getWorld().getSuccessorsForPathfinding(ant.getTile(), true);

        boolean orderIssued = false;
        // go to the saftiest tile
        do {
            if (list.size() > 0) {
                int bestSafety = -99999;
                SearchTarget safteyTile = null;
                for (SearchTarget t : list) {
                    int safety = (Ants.getInfluenceMap().getSafety(ant.getTile()));
                    // LOGGER.info("compare safety %s vs %s", safety, bestSafety);
                    if (safety > bestSafety) {
                        safteyTile = t.getTargetTile();
                        bestSafety = safety;
                    }
                }
                list.remove(safteyTile);
                if (putMissionOrder(ant, safteyTile.getTargetTile())) {
                    ant.getPath().add(0, ant.getTile());
                    orderIssued = true;
                    LOGGER.info(
                            "AttackHillMission:dangerous here! move ant %s backwards to %s. (Safety is: %s) enemyHill %s",
                            ant, safteyTile.getTargetTile(), bestSafety, enemyHill);
                }
            } else {
                putMissionOrder(ant);
                orderIssued = true;
            }

        } while (!orderIssued);
    }

    private boolean moveToSide(Ant ant) {
        List<Aim> aims = Aim.getOrthogonalAims((Ants.getWorld().getDirections(ant.getTile(), enemyHill).get(0)));
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
        Tile tile = ant.getPath().get(0);
        Aim currentAim = Ants.getWorld().getDirections(ant.getTile(), tile).get(0);

        for (Aim a : Aim.getOrthogonalAims(currentAim)) {
            if (ants.contains(Ants.getWorld().getTile(tile, a))) {
                LOGGER.info("there is an ant on the attackline");
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
