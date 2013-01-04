package ants.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import search.Barrier;
import search.BreadthFirstSearch;
import search.BreadthFirstSearch.FrontierTest;
import search.BreadthFirstSearch.GoalTest;
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
    private int controlAreaRadius2 = Math.max((int) Math.sqrt(Ants.getWorld().getViewRadius2() + 4), 49);
    private int guardHillTurn = Ants.getProfile().getDefendHills_StartTurn();
    private int antsMoreThanEnemy = 1;
    private boolean needsMoreAnts;

    private enum DefendMode {
        Barrier,
        Default
    };

    private DefendMode mode = DefendMode.Default;

    public DefendHillMission(Tile myhill) {
        this.hill = myhill;
        BreadthFirstSearch bfs = new BreadthFirstSearch(Ants.getWorld());
        tilesAroundHill = bfs.floodFill(myhill, controlAreaRadius2);
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), myhill, "DefendArea: %s", tilesAroundHill);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void execute() {
        determineMode();

        if (mode == DefendMode.Barrier) {
            defendBarrier();
        } else {
            defendHill();
        }
    }

    private void defendBarrier() {
        List<Tile> enemyNearBy = getEnemyAntsNearby();
        boolean hasAttackers = enemyNearBy.size() > 0;
        gatherAnts(barrier.getBarrier().size() - ants.size(), hasAttackers);
        if (!hasAttackers)
            releaseAnts(ants.size() - barrier.getBarrier().size() / 2);

        if (enemyNearBy.size() > ants.size()) {
            closeBarrier();
        } else {
            List<Ant> antswithOder = new ArrayList<Ant>();
            placeAntOnBarrier(antswithOder, 0);
            placeAntOnBarrier(antswithOder, 1);

        }
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill,
                "DefendHillMission attackers are: %s <br/> Defenders are: %s Need help: %s", enemyNearBy, ants,
                needsMoreAnts);
    }

    private void closeBarrier() {

        List<Tile> bar = barrier.getBarrier();
        List<Tile> secondLine = getPlaceTiles(1);
        List<Tile> inversePlaces = barrier.getBarrier();
        inversePlaces.removeAll(getPlaceTiles(0));

        for (Ant a : ants) {
            if (bar.contains(a.getTile())) {
                for (Tile t : inversePlaces) {
                    if (Ants.getWorld().manhattanDistance(a.getTile(), a.getTile()) == 1) {
                        if (putMissionOrder(a, t))
                            break;
                    }
                }
            } else if (secondLine.contains(a.getTile())) {
                putMissionOrder(a, barrier.getAimOfBarrier());
            } else {
                moveToNextTileOnPath(a);
            }
        }

    }

    /**
     * 
     * @param antswithOrder
     * @param barrierLevel
     *            barrier behind the actual barrier.
     */
    private void placeAntOnBarrier(List<Ant> antswithOrder, int barrierLevel) {
        for (Ant a : ants) {
            if (antswithOrder.contains(a))
                continue;

            if (barrier.getBarrier().contains(a.getTile())) {
                putMissionOrder(a);
                antswithOrder.add(a);
                continue;
            }
            if (!moveToNextTileOnPath(a)) {
                a.setPath(null);
            } else {
                antswithOrder.add(a);
                continue;
            }

            for (Tile t : getPlaceTiles(barrierLevel)) {
                if (Ants.getWorld().getIlk(t) == Ilk.MY_ANT)
                    continue;

                List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, a.getTile(), t);
                if (path == null)
                    continue;
                a.setPath(path);
                moveToNextTileOnPath(a);
                antswithOrder.add(a);
                break;
            }
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

        if (Ants.getAnts().getTurn() == 10) {
            AntsBreadthFirstSearch bfs = new AntsBreadthFirstSearch(Ants.getWorld());
            barrier = bfs.getBarrier(hill, Ants.getWorld().getViewRadius2(), 5);
            if (barrier != null) {
                mode = DefendMode.Barrier;
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
                + controlAreaRadius2 + " tiles");

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

        if (attackers.size() == 0 && Ants.getAnts().getTurn() > guardHillTurn) {
            // if there are no attackers but late in game (guardHillTurn) we gather 1 ant for protection
            gatherAntsAmount = 1 - ants.size();
        } else if (attackers.size() > 0) {
            gatherAntsAmount = attackers.size() - ants.size() + antsMoreThanEnemy;
        }
        if (gatherAntsAmount > 0)
            gatherAnts(gatherAntsAmount, attackers.size() > 0);
        else {
            needsMoreAnts = false;
            releaseAnts(Math.abs(gatherAntsAmount));
        }
    }

    private void gatherAnts(int amount, boolean hasAttackers) {
        Map<Ant, List<Tile>> antsNearBy = gatherAnts(hill, amount, controlAreaRadius2);
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
