package ants.missions;

import java.util.*;

import logging.*;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.*;
import ants.state.*;
import api.entities.*;

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

        LOGGER.info("TroopMission_created TroopPoint %s Ants needed %S", troopPoint, amount);
    }

    @Override
    public boolean isComplete() {
        if (ants.size() < amount)
            return false;

        for (Ant a : ants) {
            // TODO define max manhattanDistance considering amount
            int maxDistance = (amount / 4); // vierer nachbarschaft.
            if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) > maxDistance) {
                LOGGER.info("Ant %s it to far away of TroopPoint %s. mission insn't compelete yet", a, troopPoint);
                return false;
            }
        }

        LOGGER.info("TroopMission_completed TroopPoint %s Ants needed %S Ants: %s", troopPoint, amount, ants);
        return true;
    }

    @Override
    public void execute() {
        LOGGER.info("TroopMission_Executed TroopPoint %s Ants:  %s", troopPoint, ants.toString());
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
            // diffx += Math.abs(a.getTile().getCol() - troopPoint.getCol());
            diffx += a.getTile().getCol();
            // diffy += Math.abs(a.getTile().getRow() - troopPoint.getRow());
            diffy += a.getTile().getRow();
        }

        int troopPointFactor = ants.size();

        diffx += troopPoint.getCol() * troopPointFactor;
        diffy += troopPoint.getRow() * troopPointFactor;

        diffx = diffx / (ants.size() + troopPointFactor);
        diffy = diffy / (ants.size() + troopPointFactor);

        offsetPoint = new Tile((int) diffy, (int) diffx);
        LOGGER.info("TroopMission_calculateBalancePoint x: %s y: %s offsetPoint %s", diffx, diffy, offsetPoint);
    }

    private void moveAnt(Ant a) {
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
                LOGGER.info("TroopMission_AntMoved %s moved to %s", a, aim);
                bIssueOrderd = true;
            }
        }
        if (!bIssueOrderd && !a.getTile().equals(troopPoint)) {
            // move towards TroopPoint
            List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), troopPoint);
            Collections.shuffle(aims);
            for (Aim ai : aims) {
                if (putMissionOrder(a, ai)) {
                    LOGGER.info("TroopMission_AntMoved %s moved to %s", a, ai);
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
                    LOGGER.info("TroopMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        }
        if (!bIssueOrderd) {
            // if no issue could be ordered we do a null move, so that the ant doesn't apear at the
            // unemployeed list
            putMissionOrder(a, (Aim) null);
        }
    }

    private void gatherAnts() {
        LOGGER.info("TroopMission_gatherAnts TroopPoint %s", troopPoint);
        for (Ant a : Ants.getPopulation().getMyUnemployedAnts()) {
            if (a.getMission() != null)
                continue;
            if (ants.size() == amount)
                break;

            if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) > attractionDistance) {
                LOGGER.info("Ant %s it to far away for TroopPoint %s, cant join mission", a, troopPoint);
                continue; // to far away
            }
            List<Tile> p = Ants.getPathFinder().search(Strategy.Simple, a.getTile(), troopPoint);
            if (p == null)
                continue;

            // TODO store this path?
            // A path is found we add the ant to our mission
            LOGGER.info("TroopMission_NewAnt %s is joining for TroopPoint %s", a, troopPoint);
            ants.add(a);
            moveAnt(a);
        }
    }

    public String toString() {
        return String.format("TroopMission Point: %s Ants %s", troopPoint, ants);
    }

    @Override
    protected boolean isSpecificMissionValid() {
        return true;
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

}
