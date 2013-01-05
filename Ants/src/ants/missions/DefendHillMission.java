package ants.missions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import search.Barrier;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.FrontierTest;
import search.BreadthFirstSearch.GoalTest;
import tactics.combat.AttackingCombatPositioning;
import tactics.combat.CombatPositioning;
import tactics.combat.DefendingCombatPositioning;
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

public class DefendHillMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.DEFEND_HILL);

    private Tile hill = null;
    private List<Tile> tilesAroundHill = new ArrayList<Tile>();
    private Barrier barrier = null;
    final private int CONTROL_AREA_RADIUS2 = Math.max((int) Math.sqrt(Ants.getWorld().getViewRadius2() + 4), 49);
    final private int START_DEFENDHILL_TURN = Ants.getProfile().getDefendHills_StartTurn();
    final private int DEFENDER_MORETHAN_ATTACKERS = 1;
    final private int GATHERANTS_MANHATTAN_DISTANCE = 20;
    final private boolean USEBARRIER = false;
    private boolean barrierClosed = false;
    private boolean needsMoreAnts;

    private enum DefendMode {
        Barrier,
        Default
    };

    private DefendMode mode = DefendMode.Default;

    public DefendHillMission(Tile myhill) {
        this.hill = myhill;
        BreadthFirstSearch bfs = new BreadthFirstSearch(Ants.getWorld());
        tilesAroundHill = bfs.floodFill(myhill, CONTROL_AREA_RADIUS2);
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), myhill, "DefendArea: %s", tilesAroundHill);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void execute() {
        LOGGER.trace("determineMode %s", hill);
        determineMode();

        if (mode == DefendMode.Barrier) {
            LOGGER.trace("defendBarrier %s", hill);
            defendBarrier();
        } else {
            LOGGER.trace("defendHill %s", hill);
            defendHill();
        }
        LOGGER.trace("end execute() defendHillMission %s", hill);
    }

    private void defendBarrier() {
        List<Tile> enemyNearBy = getEnemyAntsNearby();
        boolean hasAttackers = enemyNearBy.size() > 0;
        gatherAnts(barrier.getBarrier().size() - ants.size(), hasAttackers);

        if (!hasAttackers) {
            releaseAnts(ants.size() - barrier.getBarrier().size() / 2);
            barrierClosed = false;
        }
        if (enemyNearBy.size() > barrier.getBarrierPlaceTiles().size()) {
            if (!barrierClosed) {
                LOGGER.trace("closeBarrier %s", hill);
                closeBarrier();
            } else {
                // if barrier is closed we have to attack.
                Tile attackDirection = Ants.getWorld().getTile(
                        barrier.getBarrier().get(barrier.getBarrier().size() / 2), barrier.getAimOfBarrier());
                CombatPositioning cp = new AttackingCombatPositioning(attackDirection, Ants.getWorld(),
                        Ants.getInfluenceMap(), new ArrayList<Unit>(ants), enemyNearBy);
                for (Ant ant : ants) {
                    putMissionOrder(ant, cp.getNextTile(ant));
                }
                LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, "ACP: " + cp.getLog());
            }
            barrierClosed = true;
        } else {
            placeAntAtBarrier(ants);
            barrierClosed = false;
        }
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill,
                "DefendHillMission attackers are: %s <br/> Defenders are: %s Need help: %s", enemyNearBy, ants,
                needsMoreAnts);
    }

    private void closeBarrier() {
        List<Ant> antswithOrder = new ArrayList<Ant>();
        List<Tile> bar = barrier.getBarrier();
        List<Tile> secondLine = getPlaceTiles(1);
        List<Tile> inversePlaces = barrier.getBarrier();
        inversePlaces.removeAll(getPlaceTiles(0));
        LOGGER.trace("inversePlaces: %s", inversePlaces);
        for (Ant a : ants) {
            if (bar.contains(a.getTile())) {
                boolean hasAntBehind = Ants.getWorld().getIlk(
                        Ants.getWorld().getTile(a.getTile(), Aim.getOpposite(barrier.getAimOfBarrier()))) == Ilk.MY_ANT;
                if (!hasAntBehind) {
                    putMissionOrder(a);
                    LOGGER.trace("no ant behind do not move: %s", a);
                    continue;
                }
                for (Tile t : inversePlaces) {

                    LOGGER.trace("manhattanDistance: %s %s to %s", Ants.getWorld().manhattanDistance(a.getTile(), t),
                            t, a.getTile());
                    if (Ants.getWorld().manhattanDistance(a.getTile(), t) == 1) {
                        if (putMissionOrder(a, t))
                            break;
                    }
                }
            }
        }
        for (Ant a : ants) {
            if (antswithOrder.contains(a))
                continue;

            if (secondLine.contains(a.getTile())) {
                LOGGER.trace("ant on secondLine: %s", a);
                if (!putMissionOrder(a, barrier.getAimOfBarrier())) {
                    LOGGER.trace("cannot move ant from secondLine to front: %s", a);
                }
            } else {
                placeAntAtBarrier(Arrays.asList(new Ant[] { a }));
            }

        }

    }

    /**
     * 
     * @param antswithOrder
     * @param barrierLevel
     *            barrier behind the actual barrier.
     */
    private void placeAntAtBarrier(List<Ant> antsToPlace) {
        List<Tile> placeTiles = new ArrayList<Tile>();
        if (barrierClosed) {
            placeTiles = barrier.getBarrier();
        } else {
            placeTiles = getPlaceTiles(0);
        }
        placeTiles.addAll(getPlaceTiles(1));

        List<Tile> correctPlacedAnts = new ArrayList<Tile>(placeTiles);

        while (placeTiles.size() > 0) {
            if (Ants.getWorld().getIlk(placeTiles.get(0)) == Ilk.MY_ANT) {
                placeTiles.remove(0);
            } else {
                break;
            }

        }
        LOGGER.trace("placeTiles shrinked: %s", placeTiles);
        correctPlacedAnts.removeAll(placeTiles);
        int i = 2;
        for (Ant a : antsToPlace) {
            if (correctPlacedAnts.contains(a.getTile())) {
                putMissionOrder(a);
                continue;
            }
            if (placeTiles.size() > 0) {
                placeTiles.addAll(getPlaceTiles(i));
            }
            if (placeTiles.size() > 0) {
                Tile t = placeTiles.get(0);
                List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, a.getTile(), t);
                if (path != null && path.size() > 1) {
                    LOGGER.trace("path for ant %s shrinked: %s", a, path);
                    putMissionOrder(a, path.get(1));
                }
                continue;
            }
            doMoveInDirection(a, getPlaceTiles(i).get(0));

        }
    }

    public List<Tile> getPlaceTiles(int barrierLevel) {
        if (barrierLevel == 0)
            return barrier.getBarrierPlaceTiles();

        List<Tile> offsetBarrier = new ArrayList<Tile>();

        Tile offSet = null;
        Aim a = barrier.getAimOfBarrier();
        if (a == Aim.NORTH || a == Aim.SOUTH)
            offSet = new Tile(a == Aim.SOUTH ? -barrierLevel : barrierLevel, 0);
        else
            offSet = new Tile(0, a == Aim.EAST ? -barrierLevel : barrierLevel);

        for (Tile t : barrier.getBarrierPlaceTiles()) {
            offsetBarrier.add(Ants.getWorld().getTile(t, offSet));
        }
        return offsetBarrier;
    }

    private void defendHill() {
        List<Tile> enemyNearBy = getEnemyAntsNearby();
        boolean hasAttackers = enemyNearBy.size() > 0;
        gatherOrReleaseAnts(enemyNearBy);

        // unfortunately we don't have any ant to defend.
        if (ants.size() == 0)
            return;

        if (hasAttackers) {
            resetAnts();
            String attInfo = "";
            for (Tile a : enemyNearBy)
                attInfo += "<br/> " + a;
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill,
                    "DefendHillMission attackers are: %s <br/> Defenders are: %s Need help: %s", attInfo, ants,
                    needsMoreAnts);
            CombatPositioning cp = new DefendingCombatPositioning(hill, Ants.getWorld(), Ants.getInfluenceMap(),
                    new ArrayList<Unit>(ants), enemyNearBy);
            for (Ant ant : ants) {
                putMissionOrder(ant, cp.getNextTile(ant));
            }
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, "DCP: " + cp.getLog());
        } else {
            keepAntDoingStuff();
        }
    }

    private void determineMode() {

        if (!USEBARRIER)
            return;

        if (Ants.getAnts().getTurn() == 10) {
            AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
            barrier = bfs.getBarrier(hill, Ants.getWorld().getViewRadius2(), 5);
            if (barrier != null) {
                mode = DefendMode.Barrier;
            }
        } else if (Ants.getAnts().getTurn() > 10 && mode == DefendMode.Barrier) {
            // if we switched to barrier defend-mode we have to check if we aren't overrun.
            // if we are so we switch back to the default mode.
            for (Tile t : barrier.getBarrier()) {
                if (Ants.getWorld().getIlk(t) == Ilk.ENEMY_ANT) {
                    mode = DefendMode.Default;
                    break;
                }
            }
        }

        String msg = "DefendMode is "
                + mode
                + (mode == DefendMode.Barrier ? " in direction " + barrier.getAimOfBarrier() + ": " + getPlaceTiles(0)
                        + ";" + getPlaceTiles(1) : "");
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, msg);

    }

    private void resetAnts() {
        for (Ant a : ants) {
            if (a.hasPath()) {
                Ants.getOrders().getAntsOnFood().remove(a);
                a.setPath(null);
            }
        }
    }

    private void keepAntDoingStuff() {

        AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
        Tile food = bfs.findSingleClosestTile(hill, tilesAroundHill.size(), new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).isFood() && !Ants.getOrders().isFoodTargeted(tile);
            }
        });
        Tile closestToFood = null;
        if (food != null) {
            closestToFood = bfs.findSingleClosestTile(food, tilesAroundHill.size(), new GoalTest() {
                @Override
                public boolean isGoal(Tile tile) {
                    return getAnts().contains(new Ant(tile, 0));
                }
            });
        }
        List<Ant> antsToRelease = new ArrayList<Ant>();
        for (Ant a : getAnts()) {
            if (closestToFood != null && a.getTile().equals(closestToFood)) {
                antsToRelease.add(a);
            } else {
                defaultDefendHillMove(a);
            }
        }
        removeAnts(antsToRelease);
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, "DefendHillMission no attackers near by -+ "
                + CONTROL_AREA_RADIUS2 + " tiles");

    }

    private void defaultDefendHillMove(Ant a) {
        if (a.getTile().equals(hill)) {
            if (doAnyMove(a))
                return;
        }
        if (Ants.getWorld().manhattanDistance(hill, a.getTile()) > 2) {
            if (doMoveInDirection(a, hill))
                return;
        }
        putMissionOrder(a);
    }

    private void gatherOrReleaseAnts(List<Tile> attackers) {
        int gatherAntsAmount = 0;

        if (attackers.size() == 0 && Ants.getAnts().getTurn() > START_DEFENDHILL_TURN) {
            // if there are no attackers but late in game (guardHillTurn) we gather 1 ant for protection
            gatherAntsAmount = 1 - ants.size();
        } else if (attackers.size() > 0) {
            gatherAntsAmount = attackers.size() - ants.size() + DEFENDER_MORETHAN_ATTACKERS;
        }
        if (gatherAntsAmount > 0) {
            gatherAnts(gatherAntsAmount, attackers.size() > 0);
        } else {
            needsMoreAnts = false;
            releaseAnts(Math.abs(gatherAntsAmount));
        }
    }

    private void gatherAnts(int amount, boolean hasAttackers) {
        Map<Ant, List<Tile>> antsNearBy = gatherAnts(hill, amount, GATHERANTS_MANHATTAN_DISTANCE);
        LOGGER.debug("gatherAnts: New ants %s for mission: %s (needed: %s)", antsNearBy.keySet(), this, amount);
        if (antsNearBy.keySet().size() < amount)
            needsMoreAnts = hasAttackers;
        for (Ant a : antsNearBy.keySet()) {
            ants.add(a);
        }

    }

    /**
     * if mode is Barrier it returns the enemies near the barrier, else the enemies near the hill
     * 
     * @return enemies near defense
     */
    private List<Tile> getEnemyAntsNearby() {
        List<Tile> enemies = new ArrayList<Tile>();
        if (mode == DefendMode.Default) {
            for (Tile around : tilesAroundHill) {
                if (Ants.getWorld().getIlk(around).hasEnemyAnt()) {
                    enemies.add(around);
                }
            }
        } else {
            final List<Tile> bar = barrier.getBarrier();
            Tile start = Ants.getWorld().getTile(bar.get(bar.size() / 2), barrier.getAimOfBarrier());
            AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
            enemies = bfs.findClosestTiles(start, Integer.MAX_VALUE, Integer.MAX_VALUE, Ants.getWorld()
                    .getViewRadius2(), new GoalTest() {

                @Override
                public boolean isGoal(Tile tile) {
                    return Ants.getWorld().getIlk(tile) == Ilk.ENEMY_ANT;
                }
            }, new FrontierTest() {
                @Override
                public boolean isFrontier(Tile tile) {
                    return !bar.contains(tile);
                }
            });
            if (enemies == null)
                enemies = new ArrayList<Tile>();
        }
        return enemies;
    }

    public boolean needsMoreAnts() {
        return needsMoreAnts;
    }

    @Override
    protected String isSpecificMissionValid() {
        if (!Ants.getWorld().getMyHills().contains(hill))
            return "Our hill " + hill + " is no longer there";
        return null;
    }

    @Override
    protected boolean checkAnts() {
        return false;
    }

    public Tile getHill() {
        return this.hill;
    }

    @Override
    public Type getTaskType() {
        return Type.DEFEND_HILL;
    }

    @Override
    public String toString() {
        return "DefendMission " + getHill();
    }

}
