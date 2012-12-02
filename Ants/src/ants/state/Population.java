package ants.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.tasks.Task.Type;
import api.entities.Tile;

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

    private Map<Type, Integer> maxResourcesPerTask = new HashMap<Type, Integer>();
    private Map<Type, Integer> usedResourcesPerTask = new HashMap<Type, Integer>();

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
        // reset used resources to 0
        for (Type taskType : Type.values())
            usedResourcesPerTask.put(taskType, 0);
    }

    public void setMaxResources(Type taskType, Integer maxResources) {
        this.maxResourcesPerTask.put(taskType, maxResources);
    }

    public Integer getMaxResources(Type taskType) {
        return this.maxResourcesPerTask.get(taskType);
    }

    public void incrementUsedResources(Type taskType, int numberOfAnts) {
        final Integer usedResources = usedResourcesPerTask.get(taskType);
        final Integer availableResources = getMaxAnts(taskType);

        if ((availableResources - usedResources) < 0)
            throw new InsufficientResourcesException("Task " + taskType + "s tried to mark too many ants as used");

        usedResourcesPerTask.put(taskType, usedResources + numberOfAnts);
    }

    public boolean isNumberOfAntsAvailable(Type taskType, int numberOfAnts) {
        final Integer usedResources = usedResourcesPerTask.get(taskType);
        final Integer availableResources = getMaxAnts(taskType);
        return numberOfAnts <= (availableResources - usedResources);
    }

    public Integer getMaxAnts(Type taskType) {
        return (int) Math.ceil(getMyAnts().size() * (getMaxResources(taskType) / 100.0));
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

    public Ant getMyAntAt(Tile tile, boolean unemployedOnly) {
        Collection<Ant> ants = unemployedOnly ? getMyUnemployedAnts() : myAnts;
        for (Ant ant : ants) {
            if (ant.getTile().equals(tile))
                return ant;
        }
        return null;
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
                LOGGER.debug("Could not remove ant %s Tile: %s of unemployedAnts : %s employedAnts %s, myAnts %s", ant,
                        ant.getTile(), myUnemployedAnts, employedAnts, myAnts);
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

    public void removeEmployedAnt(Ant a) {
        employedAnts.remove(a);
        myUnemployedAnts.add(a);
    }

    public Set<Ant> getEmployedAnts() {
        return employedAnts;
    }

}
