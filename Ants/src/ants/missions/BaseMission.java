package ants.missions;

import java.util.*;

import logging.*;
import ants.LogCategory;
import ants.entities.*;
import ants.state.*;
import api.entities.*;

/***
 * Implements the interface Mission an handles the base tasks of a mission.
 * 
 * @author kaeserst
 * 
 */
public abstract class BaseMission implements Mission {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    protected List<Ant> ants;
    private boolean abandon = false;

    public BaseMission(Ant... ants) {
        this.ants = new ArrayList<Ant>();
        for (Ant ant : ants) {
            this.ants.add(ant);
        }
    }

    @Override
    public boolean isValid() {
        if (abandon)
            return false;
        for (Ant ant : this.ants) {
            if (!isAntAlive(ant))
                return false;
        }
        return isSpecificMissionValid();
    }

    private boolean isAntAlive(Ant ant) {
        boolean antIsAlive = (Ants.getWorld().getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            LOGGER.debug("isMissionValid(): no ant at %s", ant.getTile());
        }
        return antIsAlive;
    }

    /***
     * cancels the mission.
     */
    protected void abandonMission() {
        LOGGER.debug("Abandoning mission of type %s", getClass().getSimpleName());
        abandon = true;
    }

    /***
     * puts the next move of the ant in the orders set.
     * 
     * @param ant
     * @param aim
     * @return true if the move is stored and will be executed.
     */
    private boolean putMissionOrder(Ant ant, Aim aim) {
        return Ants.getOrders().issueOrder(ant, aim, getVisualizeInfos());
    }

    /***
     * 
     * @return the name of the mission.
     */
    protected String getVisualizeInfos() {
        return getClass().getSimpleName();
    }

    /***
     * Abstract class must be implemented be the mission. it checks if the mission is still vaild.
     * 
     * @return true if mission is vaild
     */
    protected abstract boolean isSpecificMissionValid();

    protected boolean putMissionOrder(Ant a, Tile to) {

        List<Aim> aims = Ants.getWorld().getDirections(a.getTile(), to);
        if (aims.size() != 1)
            throw new RuntimeException(String.format("Ant cannot move from %s to %s", a.getTile(), to));

        return putMissionOrder(a, aims.get(0));

    }

    public void setup() {
        for (Ant ant : this.ants) {
            ant.setup();
        }
    }
}
