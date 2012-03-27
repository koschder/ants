package starter.mission;

import starter.Ant;
import starter.Ants;
import starter.Move;

public abstract class Mission {
    protected Ant ant;
    protected Ants ants;
    
    public abstract boolean IsMissionComplete();
    protected abstract boolean IsSpecificMissionValid();
    public abstract void proceedMission();
    
    public final boolean IsMissionValid(){
        //TODO check if ant is alive.
        boolean antIsAlive = (ants.getIlk(ant.getTile()).hasFriendlyAnt());
        return (antIsAlive && IsSpecificMissionValid());
    }
    
    public void InitMission(Ants ants){
        this.ants = ants;
    }
    
    public Mission(Ant ant,Ants ants){
        this.ant = ant;
        this.ants = ants;
    }
}
