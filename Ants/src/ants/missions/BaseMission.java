package ants.missions;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public abstract class BaseMission implements Mission {
    protected Ant ant;
    // stores the last moves of the mission
    protected List<Move> lastMoves = new ArrayList<Move>();
    private boolean abandon = false;

    protected abstract boolean IsSpecificMissionValid();

    @Override
    public final boolean isValid() {
        if (abandon)
            return false;
        boolean antIsAlive = (Ants.getWorld().getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "isMissionValid(): no ant at %s", ant.getTile());
        }
        return (antIsAlive && IsSpecificMissionValid());
    }

    public BaseMission(Ant ant) {
        this.ant = ant;
    }

    protected void abandonMission() {
        Logger.debug(LogCategory.EXECUTE_MISSIONS, "Abandoning mission of type %s", getClass().getSimpleName());
        abandon = true;
    }

    /***
     * 
     * @return Last move of this mission or null if no move is performed yet.
     */
    public Move getLastMove() {
        if (lastMoves.size() == 0)
            return null;
        return lastMoves.get(this.lastMoves.size() - 1);
    }

    protected boolean putMissionOrder(Ant ant, Aim aim) {
        if (Ants.getOrders().putOrder(ant, aim)) {
            lastMoves.add(new Move(ant.getTile(), aim));
            return true;
        }
        return false;
    }

    public Ant getAnt() {
        return ant;
    }
}
