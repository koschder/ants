package ants.missions;

import java.util.ArrayList;
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
import search.BreadthFirstSearch.GoalTest;
import ants.LogCategory;
import ants.entities.Ant;
import ants.search.AntsBreadthFirstSearch;
import ants.state.Ants;
import ants.tasks.Task.Type;
import ants.util.LiveInfo;
import api.entities.Aim;
import api.entities.Tile;

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
            defending(enemyNearBy);
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
            // if there are no attackers but late in game (guardHillTurn) we gahter 1 ant for protection
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

    private void defending(List<Tile> enemyNearBy) {
        String attInfo = "";
        Map<Aim, List<Tile>> attackers = attackersByDirection(enemyNearBy);
        for (Entry<Aim, List<Tile>> a : attackers.entrySet())
            attInfo += "<br/> " + a;

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill,
                "DefendHillMission attackers are: %s <br/> Defenders are: %s Need help: %s", attInfo, ants,
                needsMoreAnts);
        List<Ant> antsWithOrder = new ArrayList<Ant>();

        boolean orderDone = true;
        while (orderDone) {
            int antsWithOrderCount = antsWithOrder.size();
            for (Entry<Aim, List<Tile>> attackDirection : attackers.entrySet()) {
                int antsInDirection = 0;
                for (Ant a : ants) {
                    if (antsWithOrder.contains(a))
                        continue;
                    if (putDefendOrder(a, attackDirection.getKey())) {
                        antsInDirection++;
                        antsWithOrder.add(a);
                    }
                    if (antsInDirection - 1 == attackDirection.getValue().size())
                        break;
                }
            }
            orderDone = antsWithOrderCount < antsWithOrder.size();
        }
        for (Ant a : ants) {
            if (antsWithOrder.contains(a))
                continue;
            defaultDefendHillMove(a);
        }

    }

    private boolean putDefendOrder(Ant ant, Aim aim) {
        // ant is to far away from hill call it back
        if (Ants.getWorld().manhattanDistance(ant.getTile(), hill) > 4) {
            for (Aim ai : Ants.getWorld().getDirections(ant.getTile(), hill)) {
                if (putMissionOrder(ant, ai)) {
                    return true;
                }
            }
        }

        // we want to defend two tiles away from our hill
        Tile t = Ants.getWorld().getTile(hill, aim);
        t = Ants.getWorld().getTile(t, aim);

        if (t.equals(ant.getTile()))
            return putMissionOrder(ant);

        if ((aim == Aim.NORTH || aim == Aim.SOUTH) && ant.getTile().getRow() == t.getRow()) {
            // is on correct row, move to middle or stay
            if (!putMissionOrder(ant, Ants.getWorld().getDirections(ant.getTile(), t).get(0))) {
                return putMissionOrder(ant);
            } else {
                return true;
            }
        }
        if ((aim == Aim.EAST || aim == Aim.WEST) && ant.getTile().getCol() == t.getCol()) {
            // is on correct column, move to middle stay
            if (!putMissionOrder(ant, Ants.getWorld().getDirections(ant.getTile(), t).get(0))) {
                return putMissionOrder(ant);
            } else {
                return true;
            }
        }

        return putMissionOrder(ant, aim);
    }

    private Map<Aim, List<Tile>> attackersByDirection(List<Tile> tiles) {

        Map<Aim, List<Tile>> attackers = new HashMap<Aim, List<Tile>>();
        TreeMap<Aim, List<Tile>> sortedAttackers = new TreeMap<Aim, List<Tile>>(new ValueComparator(attackers));

        for (Tile t : tiles) {
            Aim attackAim = null;
            int diffx = t.getRow() - hill.getRow();
            int diffy = t.getCol() - hill.getCol();
            if (Math.abs(diffx) > Math.abs(diffy))
                attackAim = Ants.getWorld().getDirections(hill, new Tile(t.getRow(), hill.getCol())).get(0);
            else
                attackAim = Ants.getWorld().getDirections(hill, new Tile(hill.getRow(), t.getCol())).get(0);

            List<Tile> l = new ArrayList<Tile>();
            if (attackers.containsKey(attackAim)) {
                l = attackers.remove(attackAim);
            }
            l.add(t);
            attackers.put(attackAim, l);
        }
        LOGGER.debug("Attackers %s from directions %s ", tiles, attackers);
        sortedAttackers.putAll(attackers);
        return sortedAttackers;
    }

    class ValueComparator implements Comparator<Aim> {

        Map<Aim, List<Tile>> base;

        public ValueComparator(Map<Aim, List<Tile>> base) {
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
