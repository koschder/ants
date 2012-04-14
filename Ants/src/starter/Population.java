package starter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import starter.Logger.LogCategory;

public class Population {
    private Set<Ant> myAnts = new HashSet<Ant>();
    private Set<Ant> myUnemployedAnts = null;
    private Set<Ant> employedAnts = new HashSet<Ant>();

    private Set<Ant> enemyAnts = new HashSet<Ant>();

    public Set<Ant> getMyAnts() {
        return myAnts;
    }

    public Set<Ant> getEnemyAnts() {
        return enemyAnts;
    }

    public void clearMyAnts() {
        myAnts.clear();
        myUnemployedAnts = null;
    }

    public Collection<Ant> getMyUnemployedAnts() {
        if (myUnemployedAnts == null)
            myUnemployedAnts = new HashSet<Ant>(myAnts);
        for (Iterator<Ant> it = employedAnts.iterator(); it.hasNext();) {
            Ant ant = it.next();
            if (!myUnemployedAnts.remove(ant)) {
                Logger.error(LogCategory.SETUP, "Could not remove ant %s Tile: %s of unemployedAnts size: %s", ant,
                        ant.getTile(), myUnemployedAnts.size());
            } else {
                Logger.debug(LogCategory.SETUP, "Ant %s Tile: %s marked as employed", ant, ant.getTile());
            }
            it.remove();
        }
        return myUnemployedAnts;
    }

    public void addEmployedAnt(Ant ant) {
        employedAnts.add(ant);
    }

}
