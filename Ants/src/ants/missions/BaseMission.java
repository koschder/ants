package ants.missions;

import java.util.ArrayDeque;
import java.util.Deque;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import api.entities.Aim;

/***
 * Implements the interface Mission an handles the base tasks of a mission.
 * 
 * @author kaeserst
 * 
 */
public abstract class BaseMission implements Mission {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.EXECUTE_MISSIONS);
    protected Ant ant;
    protected Deque<Move> previousMoves = new ArrayDeque<Move>();
    private boolean abandon = false;

    public BaseMission(Ant ant) {
        this.ant = ant;
    }

    @Override
    public final boolean isValid() {
        if (abandon)
            return false;
        boolean antIsAlive = (Ants.getWorld().getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            LOGGER.debug("isMissionValid(): no ant at %s", ant.getTile());
        }
        return (antIsAlive && isSpecificMissionValid());
    }

    /***
     * 
     * @return Last move of this mission or null if no move is performed yet.
     */
    public Move getLastMove() {
        return previousMoves.isEmpty() ? null : previousMoves.getLast();
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
    protected boolean putMissionOrder(Ant ant, Aim aim) {
        if (Ants.getOrders().issueOrder(ant, aim, getVisualizeInfos())) {
            previousMoves.add(new Move(ant.getTile(), aim));
            return true;
        }
        return false;
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

    /***
     * return the ant which executes this mission
     */
    public Ant getAnt() {
        return ant;
    }
}
