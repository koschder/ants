package ants.missions;

import java.util.ArrayDeque;
import java.util.Deque;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public abstract class BaseMission implements Mission {
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
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "isMissionValid(): no ant at %s", ant.getTile());
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

    protected void abandonMission() {
        Logger.debug(LogCategory.EXECUTE_MISSIONS, "Abandoning mission of type %s", getClass().getSimpleName());
        abandon = true;
    }

    protected boolean putMissionOrder(Ant ant, Aim aim) {
        if (Ants.getOrders().putOrder(ant, aim)) {
            previousMoves.add(new Move(ant.getTile(), aim));
            return true;
        }
        return false;
    }

    protected abstract boolean isSpecificMissionValid();

    public Ant getAnt() {
        return ant;
    }
}
