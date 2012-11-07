package ants.missions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Aim;
import api.entities.Move;
import api.entities.Tile;

public class ConcentrateMission implements Mission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.CONCENTRATE);

    public List<Ant> ants = new ArrayList<Ant>();
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
    public int maxAwayForMission = 20;

    /**
     * n
     * 
     * @param tP
     *            where to troop the ants
     * @param amount
     *            how many ants to troop
     */
    public ConcentrateMission(Tile tP, int a) {
        this.troopPoint = tP;
        this.amount = a;

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
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        LOGGER.info("TroopMission_Executed TroopPoint %s Ants:  %s", troopPoint, ants.toString());
        calculateBalancePoint();
        for (Ant a : ants) {
            moveAnt(a);
        }
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

    private void moveAntOld(Ant a) {
        boolean bIssueOrderd = false;
        if (!bIssueOrderd && !a.getTile().equals(troopPoint)) {
            // move towards TroopPoint
            List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), offsetPoint);
            Collections.shuffle(aims);
            for (Aim ai : aims) {
                if (Ants.getOrders().issueOrder(a, ai, "TroopMission")) {
                    LOGGER.info("TroopMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        }
        if (!bIssueOrderd) {
            // if no issue could be ordered we do a null move, so that the ant doesn't apear at the
            // unemployeed list
            Ants.getOrders().issueOrder(a, null, "TroopMission");
        }

    }

    private void moveAnt(Ant a) {
        boolean bIssueOrderd = false;
        int maxDistance = (amount / 4); // vierer nachbarschaft.
        if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) < maxDistance) {
            Aim aim = null;
            if (diffx < diffy) {
                boolean north = (diffx < 0);
                aim = north ? Aim.NORTH : Aim.SOUTH;
            } else {
                boolean east = (diffy < 0);
                aim = east ? Aim.EAST : Aim.WEST;
            }
            if (Ants.getOrders().issueOrder(a, aim, "TroopMission")) {
                LOGGER.info("TroopMission_AntMoved %s moved to %s", a, aim);
                bIssueOrderd = true;
            }
        }
        if (!bIssueOrderd && !a.getTile().equals(troopPoint)) {
            // move towards TroopPoint
            List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), troopPoint);
            Collections.shuffle(aims);
            for (Aim ai : aims) {
                if (Ants.getOrders().issueOrder(a, ai, "TroopMission")) {
                    LOGGER.info("TroopMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        } else {
            Aim[] aims = { Aim.NORTH, Aim.WEST, Aim.SOUTH, Aim.EAST };
            // Collections.shuffle(aims);
            for (Aim ai : aims) {
                if (Ants.getOrders().issueOrder(a, ai, "TroopMission")) {
                    LOGGER.info("TroopMission_AntMoved %s moved to %s", a, ai);
                    bIssueOrderd = true;
                    break;
                }
            }
        }
        if (!bIssueOrderd) {
            // if no issue could be ordered we do a null move, so that the ant doesn't apear at the
            // unemployeed list
            Ants.getOrders().issueOrder(a, null, "TroopMission");
        }
    }

    public void gatherAnts() {
        LOGGER.info("TroopMission_gatherAnts TroopPoint %s Ants %s", troopPoint, ants.toString());
        for (Ant a : Ants.getPopulation().getMyUnemployedAnts()) {
            if (ants.size() == amount)
                break;

            if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) > maxAwayForMission) {
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

    @Override
    public Ant getAnt() {
        // TODO use it like this?
        return ants.size() > 0 ? ants.get(0) : null;
    }

    @Override
    public Move getLastMove() {
        // TODO was hier
        return null;
    }

    @Override
    public void setup() {
        for (Ant a : ants) {
            LOGGER.info("TroopMission_Setup ANT %s ", a);
            a.setup();
        }

    }

    public String toString() {
        return String.format("TroopMission Point: %s Ants %s", troopPoint, ants);
    }

}
