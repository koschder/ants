package starter.mission;

import java.util.ArrayList;
import java.util.List;

import starter.Aim;
import starter.Ant;
import starter.Ants;
import starter.Logger;
import starter.Logger.LogCategory;
import starter.Move;
import starter.tasks.BaseTask;

public abstract class Mission extends BaseTask {
    protected Ant ant;
    // protected Ants ants;
    // stores the last moves of the mission
    protected List<Move> lastMoves = new ArrayList<Move>();

    public abstract boolean IsMissionComplete();

    protected abstract boolean IsSpecificMissionValid();

    // public abstract void proceedMission();

    public final boolean isMissionValid() {
        boolean antIsAlive = (ants.getIlk(ant.getTile()).hasFriendlyAnt());
        if (!antIsAlive) {
            Logger.log(LogCategory.EXECUTE_MISSIONS, "isMissionValid(): no ant at %s", ant.getTile());
        }
        return (antIsAlive && IsSpecificMissionValid());
    }

    public void InitMission(Ants ants) {
        this.ants = ants;
    }

    public Mission(Ant ant, Ants ants) {
        super();
        this.ants = ants;
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
        if (ants.putOrder(ant, aim)) {
            // TODO wird in putorder gemacht, aber f�r ant nicht �bernommen?
            ant.setNextTile(ants.getTile(ant.getTile(), aim));
            lastMoves.add(new Move(ant.getTile(), aim));
            return true;
        }
        return false;
    }

    public Ant getAnt() {
        return ant;
    }
}
