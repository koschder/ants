package ants.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import api.Tile;

/**
 * This class keeps track of our and the enemies' ant populations. It also tracks which of our ants are already employed
 * each turn.
 * 
 * @author kases1,kustl1
 * 
 */
public class Population {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.SETUP);
    private Set<Ant> myAnts = new HashSet<Ant>();
    private Set<Ant> myUnemployedAnts = null;
    private Set<Ant> employedAnts = new HashSet<Ant>();

    private Set<Ant> enemyAnts = new HashSet<Ant>();
    private Set<Integer> players;

    public Population() {
        players = new HashSet<Integer>();
        players.add(0); // that's us
    }

    /**
     * Clears all state; this class contains only turn-scoped state information.
     */
    public void clearState() {
        myAnts.clear();
        employedAnts.clear();
        enemyAnts.clear();
        myUnemployedAnts = null;
    }

    /**
     * Adds an ant to the population. The <code>owner</code> parameter determines to which collection the ant is added.
     * 
     * @param tile
     * @param owner
     */
    public void addAnt(Tile tile, int owner) {
        if (owner == Ant.MINE)
            myAnts.add(new Ant(tile, owner));
        else {
            enemyAnts.add(new Ant(tile, owner));
            players.add(owner);
        }
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public Set<Ant> getMyAnts() {
        return myAnts;
    }

    public Set<Ant> getEnemyAnts() {
        return enemyAnts;
    }

    /**
     * 
     * @return all our ants that have no orders yet.
     */
    public Collection<Ant> getMyUnemployedAnts() {
        if (myUnemployedAnts == null)
            myUnemployedAnts = new HashSet<Ant>(myAnts);
        for (Iterator<Ant> it = employedAnts.iterator(); it.hasNext();) {
            Ant ant = it.next();
            if (!myUnemployedAnts.remove(ant)) {
                LOGGER.error("Could not remove ant %s Tile: %s of unemployedAnts size: %s", ant, ant.getTile(),
                        myUnemployedAnts.size());
            } else {
                LOGGER.debug("Ant %s Tile: %s marked as employed", ant, ant.getTile());
            }
            it.remove();
        }
        return myUnemployedAnts;
    }

    /**
     * Marks the given Ant as employed.
     * 
     * @param ant
     */
    public void addEmployedAnt(Ant ant) {
        employedAnts.add(ant);
    }

}
