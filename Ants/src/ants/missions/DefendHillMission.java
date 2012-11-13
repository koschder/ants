package ants.missions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import ants.util.LiveInfo;
import api.entities.Aim;
import api.entities.Tile;

public class DefendHillMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.DEFEND_HILL);

    private Tile hill = null;

    private int nearBy = 6;

    public DefendHillMission(Tile hill) {
        this.hill = hill;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void execute() {

        List<Tile> enemyNearBy = getEnemyAntsNearby();
        String attInfo = "";

        if (enemyNearBy.size() == 0) {
            attInfo = "no attackers near by -+ " + nearBy + " tiles";
            LOGGER.debug(attInfo);
            return;
        }
        Map<Aim, List<Tile>> attackers = attackersByDirection(enemyNearBy);

        for (Entry<Aim, List<Tile>> a : attackers.entrySet())
            attInfo += "<br/> " + a;

        // if (ants.size() != 0 && enemyNearBy.size() > ants.size() * 4) { // big attack now chance to defend
        //
        // }
        if (enemyNearBy.size() > ants.size()) { // need more ants to defend
            gatherAnts(enemyNearBy.size() - ants.size() + 2); // to for reserve
        } else if (enemyNearBy.size() > ants.size()) { // need more ants to defend
            releaseAnts(ants.size() - (enemyNearBy.size() + 2)); // two for reserve
        }

        positioning(attackers);

        LiveInfo.liveInfo(Ants.getAnts().getTurn(), hill,
                "DefendHillMission attackers are: %s <br/> Defenders are: %s", attInfo, ants);
    }

    private void positioning(Map<Aim, List<Tile>> attackers) {
        List<Ant> antsWithOrder = new ArrayList<Ant>();
        boolean orderDone = true;
        while (orderDone) {
            int antsWithOrderCount = antsWithOrder.size();
            for (Entry<Aim, List<Tile>> attackDirection : attackers.entrySet()) {
                int antsInDirection = 0;
                for (Ant a : ants) {
                    if (antsWithOrder.contains(a))
                        continue;
                    if (putOrder(a, attackDirection.getKey())) {
                        antsInDirection++;
                        antsWithOrder.add(a);
                    }
                    if (antsInDirection == attackDirection.getValue().size())
                        break;
                }
            }
            orderDone = antsWithOrderCount < antsWithOrder.size();
        }
        for (Ant a : ants) {
            if (antsWithOrder.contains(a))
                continue;

            putMissionOrder(a);
        }
    }

    private boolean putOrder(Ant ant, Aim aim) {

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

    private void gatherAnts(int amount) {
        Map<Ant, List<Tile>> antsNearBy = gatherAnts(hill, amount, 10);
        LOGGER.debug("gatherAnts: New ants %s for misson: %s", antsNearBy.keySet(), this);
        for (Ant a : antsNearBy.keySet()) {
            ants.add(a);
        }

    }

    private List<Tile> getEnemyAntsNearby() {
        List<Tile> tile = new ArrayList<Tile>();
        for (int x = -nearBy; x < nearBy; x++) {
            for (int y = -nearBy; y < nearBy; y++) {
                Tile around = Ants.getWorld().getTile(hill, new Tile(x, y));
                if (Ants.getWorld().getIlk(around).hasEnemyAnt()) {
                    tile.add(around);
                    // LOGGER.debug("Tile %s has emeny ", around);
                }
            }
        }
        return tile;
    }

    @Override
    protected boolean isSpecificMissionValid() {
        if (!Ants.getWorld().getMyHills().contains(hill))
            return false;
        return true;
    }

    @Override
    protected boolean isCheckAnts() {
        return false;
    }

    public Tile getHill() {
        return this.hill;
    }

}
