package ants.missions;

import java.util.ArrayList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import api.entities.Aim;
import api.entities.Tile;

public class TroopMission implements Mission {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.TROOP);

    public List<Ant> ants = new ArrayList<Ant>();
    public Tile troopPoint;
    public int amount;
    /**
     * How far an ant can be to get involved into the mission
     */
    public int maxAwayForMission = 20;

    /**
     * 
     * @param tP
     *            where to troop the ants
     * @param amount
     *            how many ants to troop
     */
    public TroopMission(Tile tP, int a) {
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
            int maxDistance = 1 + (amount / 4); // vierer nachbarschaft.
            if (Ants.getWorld().manhattanDistance(a.getTile(), troopPoint) > maxDistance) {
                LOGGER.info("Ant %s it to far away for TroopPoint %s mission ins't compelete yet", a, troopPoint);
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
        for (Ant a : ants) {
            moveAnt(a);
        }
    }

    private void moveAnt(Ant a) {
        if (a.getTile().equals(troopPoint))
            return;
        List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), troopPoint);

        for (Aim aim : aims) {
            if (!Ants.getWorld().isPassable(Ants.getWorld().getTile(a.getTile(), aim)))
                continue;
            if (Ants.getOrders().issueOrder(a, aim, "TroopMission")) {
                LOGGER.info("TroopMission_AntMoved %s moved to %s", a, aim);
                break;
            }
        }
        Ants.getOrders().issueOrder(a, null, "TroopMission");
        return;
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
            a.setup();
        }

    }

    public String toString() {
        return String.format("TroopMission Point: %s Ants %s", troopPoint, ants);
    }

}
