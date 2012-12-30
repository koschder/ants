package ants.missions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.Task.Type;
import api.entities.Aim;
import api.entities.Tile;

public class ConcentrateMission extends BaseMission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CONCENTRATE);

    public Tile troopPoint;
    public Tile offsetPoint;
    public int amount;
    private float diffx = 0;
    private float diffy = 0;

    public enum ContcentrateType {
        Radial,
        Square,
        Line
    };

    /**
     * How far an ant can be to get involved into the mission
     */
    public int attractionDistance = 25;

    /**
     * n
     * 
     * @param tP
     *            where to troop the ants
     * @param amount
     *            how many ants to troop
     */
    public ConcentrateMission(Tile tP, int a, int attractionDistance) {
        this.troopPoint = tP;
        this.amount = a;
        this.attractionDistance = attractionDistance;

        LOGGER.info("ConcentrateMission_created TroopPoint %s Ants needed %S", troopPoint, amount);
    }

    @Override
    public boolean isComplete() {
        if (ants.size() < amount)
            return false;

        for (Ant a : ants) {
            int maxDistance = (amount / 4) + 2; // vierer nachbarschaft.
            if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) > maxDistance) {
                LOGGER.info("Ant %s it to far away of TroopPoint %s. mission isn't compelete yet", a, troopPoint);
                return false;
            }
        }

        LOGGER.info("ConcentrateMission_completed TroopPoint %s Ants needed %S Ants: %s", troopPoint, amount, ants);
        return true;
    }

    @Override
    public void execute() {
        LOGGER.info("ConcentrateMission_Executed TroopPoint %s Ants:  %s", troopPoint, ants.toString());
        calculateBalancePoint();
        for (Ant a : ants) {
            moveAnt(a);
        }
        gatherAnts();
    }

    private void calculateBalancePoint() {
        diffx = 0;
        diffy = 0;
        for (Ant a : ants) {
            diffx += a.getTile().getCol();
            diffy += a.getTile().getRow();
        }

        int troopPointFactor = ants.size();

        diffx += troopPoint.getCol() * troopPointFactor;
        diffy += troopPoint.getRow() * troopPointFactor;

        diffx = diffx / (ants.size() + troopPointFactor);
        diffy = diffy / (ants.size() + troopPointFactor);

        offsetPoint = new Tile((int) diffy, (int) diffx);
        LOGGER.info("ConcentrateMission_calculateBalancePoint x: %s y: %s offsetPoint %s", diffx, diffy, offsetPoint);
    }

    private void moveAnt(Ant a) {

        if (a.getPath() != null) {
            List<Tile> t = a.getPath();
            Tile move = t.get(0);
            if (putMissionOrder(a, move)) {
                t.remove(0);
                if (t.size() == 0) {
                    a.setPath(null);
                } else {
                    a.setPath(t);
                }
            } else {
                List<Aim> aims = Aim.getOrthogonalAims(Ants.getWorld().getDirections(a.getTile(), move).get(0));
                for (Aim aim : aims) {
                    if (putMissionOrder(a, aim)) {
                        a.setPath(Ants.getPathFinder().search(Strategy.AStar,
                                Ants.getWorld().getTile(a.getTile(), aim), troopPoint));
                        return;
                    }
                }
                putMissionOrder(a);
            }
            return;
        }

        boolean bIssueOrderd = false;
        int maxDistance = (amount / 4); // vierer nachbarschaft.
        int manhattan = Ants.getWorld().manhattanDistance(a.getTile(), troopPoint);
        if (manhattan < maxDistance) {
            Aim aim = null;
            int maxman = Math.max(manhattan, 4) / 4;
            if (Math.random() * maxman < 0.5) { // avoid to much rotation
                if (diffx > diffy) {
                    boolean north = (diffx > 0);
                    aim = north ? Aim.NORTH : Aim.SOUTH;
                } else {
                    boolean east = (diffy > 0);
                    aim = east ? Aim.EAST : Aim.WEST;
                }
            }
            if (putMissionOrder(a, aim)) {
                LOGGER.info("ConcentrateMission_AntMoved %s moved to %s", a, aim);
                bIssueOrderd = true;
            }
        }
        if (!bIssueOrderd && !a.getTile().equals(troopPoint)) {
            // move towards TroopPoint
            List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), troopPoint);
            Collections.shuffle(aims);
            for (Aim ai : aims) {
                if (putMissionOrder(a, ai)) {
                    LOGGER.info("ConcentrateMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        }
        if (!bIssueOrderd) {
            Aim[] aims = { Aim.NORTH, Aim.WEST, Aim.SOUTH, Aim.EAST };
            List<Aim> list = new ArrayList<Aim>();
            list.addAll(Arrays.asList(aims));
            Collections.shuffle(list);
            for (Aim ai : list) {
                if (putMissionOrder(a, ai)) {
                    LOGGER.info("ConcentrateMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        }
        if (!bIssueOrderd) {
            // if no issue could be ordered we do a null move, so that the ant doesn't apear at the
            // unemployeed list
            putMissionOrder(a);
        }
    }

    private void gatherAnts() {
        LOGGER.info("ConcentrateMission_gatherAnts TroopPoint %s", troopPoint);

        Map<Ant, List<Tile>> antsNearBy = gatherAnts(troopPoint, amount - ants.size(), attractionDistance);
        for (Entry<Ant, List<Tile>> entry : antsNearBy.entrySet()) {
            List<Tile> p = entry.getValue();
            if (p == null || p.size() <= 1)
                continue;
            Ant a = entry.getKey();
            LOGGER.info("ConcentrateMission_NewAnt %s is joining for ConcentratePoint %s path is %s", a, troopPoint, p);
            a.setPath(p);
            addAnt(a);
            moveAnt(a);
        }

    }

    public String toString() {
        return String.format("ConcentrateMission Point: %s Ants %s", troopPoint, ants);
    }

    @Override
    protected String isSpecificMissionValid() {
        return null;
    }

    public void setTroopPoint(Tile tile) {
        troopPoint = tile;

    }

    public Tile getOffSetPoint() {
        return offsetPoint;
    }

    public Tile getConcentratePoint() {
        return troopPoint;
    }

    @Override
    protected boolean checkAnts() {
        return false;
    }

    @Override
    protected String getVisualizeInfos() {
        return super.getVisualizeInfos() + " TroopPoint " + troopPoint;
    }

    @Override
    public Type getTaskType() {
        return Type.CONCENTRATE_ANTS;
    }
}
