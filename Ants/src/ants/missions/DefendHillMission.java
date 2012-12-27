package ants.missions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import search.BreadthFirstSearch.GoalTest;
import tactics.combat.CombatPositioning;
import tactics.combat.DefendingCombatPositioning;
import ants.LogCategory;
import ants.entities.Ant;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
import api.entities.Tile;
import api.entities.Unit;

public class DefendHillMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.DEFEND_HILL);

    private Tile hill = null;
    private Set<Tile> hillReachable = new HashSet<Tile>();
    private int controlArea = Math.max((int) Math.sqrt(Ants.getWorld().getViewRadius2() + 4), 8);
    private int guardHillTurn = 30;
    private int antsMoreThanEnemy = 1;
    private boolean needsMoreAnts;

    public DefendHillMission(Tile hill) {
        this.hill = hill;

        hillReachable = Ants.getWorld().getAreaFlooded(hill, controlArea);
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, "DefendArea: %s", hillReachable);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void execute() {

        List<Tile> enemyNearBy = getEnemyAntsNearby();

        boolean hasAttackers = enemyNearBy.size() > 0;
        gatherOrReleaseAnts(enemyNearBy);
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
            // defending(enemyNearBy);
        } else {
            keepAntDoingStuff();
        }
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
        Tile food = bfs.findSingleClosestTile(hill, hillReachable.size(), new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).isFood() && !Ants.getOrders().isFoodTargeted(tile);
            }
        });
        Tile closestToFood = null;
        if (food != null) {
            closestToFood = bfs.findSingleClosestTile(food, hillReachable.size(), new GoalTest() {
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
        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill, "DefendHillMission no attackers near by -+ " + controlArea
                + " tiles");

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
        Map<Ant, List<Tile>> antsNearBy = gatherAnts(hill, amount, controlArea);
        LOGGER.debug("gatherAnts: New ants %s for mission: %s (needed: %s)", antsNearBy.keySet(), this, amount);
        if (antsNearBy.keySet().size() < amount)
            needsMoreAnts = hasAttackers;
        for (Ant a : antsNearBy.keySet()) {
            ants.add(a);
        }

    }

    private List<Tile> getEnemyAntsNearby() {
        List<Tile> tile = new ArrayList<Tile>();
        for (Tile around : hillReachable) {
            if (Ants.getWorld().getIlk(around).hasEnemyAnt()) {
                tile.add(around);
            }
        }
        return tile;
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
