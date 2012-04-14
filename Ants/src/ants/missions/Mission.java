package ants.missions;

import java.util.ArrayList;
import java.util.List;

import ants.entities.Aim;
import ants.entities.Ant;
import ants.entities.Move;
import ants.state.Ants;
import ants.tasks.BaseTask;
import ants.util.Logger;
import ants.util.Logger.LogCategory;


public abstract class Mission extends BaseTask {
    protected Ant ant;
    // protected Ants ants;
    // stores the last moves of the mission
    protected List<Move> lastMoves = new ArrayList<Move>();

    public abstract boolean IsMissionComplete();

    protected abstract boolean IsSpecificMissionValid();

    // public abstract void proceedMission();

    public final boolean isMissionValid() {
        boolean antIsAlive = (Ants.getWorld().getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            Logger.debug(LogCategory.EXECUTE_MISSIONS, "isMissionValid(): no ant at %s", ant.getTile());
        }
        return (antIsAlive && IsSpecificMissionValid());
    }

    public Mission(Ant ant) {
        this.ant = ant;
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
        if (Ants.getAnts().putOrder(ant, aim)) {
            // TODO wird in putorder gemacht, aber f�r ant nicht �bernommen?
            ant.setNextTile(Ants.getWorld().getTile(ant.getTile(), aim));
            lastMoves.add(new Move(ant.getTile(), aim));
            return true;
        }
        return false;
    }

    public Ant getAnt() {
        return ant;
    }
}
