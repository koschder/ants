package ants.missions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;

public class FlockMission extends BaseMission {
    private Logger LOGGER = LoggerFactory.getLogger(LogCategory.FLOCKING);
    private Tile target;
    private boolean complete;

    @Override
    public boolean isComplete() {
        return complete;
    }

    public FlockMission(Tile target, Ant... ants) {
        super(ants);
        this.target = target;
    }

    public FlockMission(Tile target, Collection<Ant> ants) {
        super(ants);
        this.target = target;
    }

    @Override
    public void execute() {
        sortAnts();
        Ant leader = ants.get(0);
        List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, leader.getTile(), target);
        final Tile virtualTarget = path.get(1);
        // if (path.size() < 2) {
        if (virtualTarget.equals(target)) {
            LOGGER.debug("Reached target %s, flockMission is complete", target);
            complete = true;
            return;
        }
        if (!putMissionOrder(leader, virtualTarget)) {
            abandonMission(); // no path to target
            return;
        }
        List<Tile> flockTargets = getSortedAndPrunedFlockTargets(leader, target);

        for (Ant ant : ants) {
            if (ant.equals(leader))
                continue;
            for (Iterator<Tile> tileIter = flockTargets.iterator(); tileIter.hasNext();) {
                final Tile next = tileIter.next();
                if (next.equals(ant.getTile()))
                    continue;
                List<Tile> virtualPath = Ants.getPathFinder().search(PathFinder.Strategy.Simple, ant.getTile(), next);
                if (virtualPath != null && putMissionOrder(ant, virtualPath.get(0))) {
                    tileIter.remove();
                    break;
                }
            }
            // if an ant can't make a sensible move this turn, just mark it as employed
            Ants.getPopulation().addEmployedAnt(ant);
        }

    }

    private List<Tile> getSortedAndPrunedFlockTargets(Ant leader, Tile destination) {
        // get visible tiles, sans the leader's tile
        List<Tile> targets = Ants.getWorld().getVisibleTiles(leader);
        targets.remove(leader.getTile());
        // calculate distances to leader and destination
        Map<Tile, Integer> leaderDistances = new HashMap<Tile, Integer>();
        Map<Tile, Integer> destDistances = new HashMap<Tile, Integer>();
        for (Tile targetTile : targets) {
            final int distance = Ants.getWorld().getSquaredDistance(leader.getTile(), targetTile);
            leaderDistances.put(targetTile, distance);
            final int destDistance = Ants.getWorld().getSquaredDistance(destination, targetTile);
            destDistances.put(targetTile, destDistance);
        }
        // remove all tiles that are closer to the destination than the leader
        // (they are out of reach, as the leader is the closest ant to the destination)
        int leaderToDest = Ants.getWorld().getSquaredDistance(leader.getTile(), destination);
        for (Iterator<Tile> targetIter = targets.iterator(); targetIter.hasNext();) {
            if (destDistances.get(targetIter.next()) < leaderToDest)
                targetIter.remove();
        }
        // sort by distance to the leader
        Collections.sort(targets, new Tile.DistanceComparator(leaderDistances));
        return targets;
    }

    private void sortAnts() {
        Map<Ant, Integer> antDistances = new HashMap<Ant, Integer>();
        for (Ant ant : ants) {
            final int distance = Ants.getWorld().getSquaredDistance(ant.getTile(), target);
            antDistances.put(ant, distance);
        }
        Collections.sort(ants, new Ant.DistanceComparator(antDistances));
    }

    @Override
    protected boolean isSpecificMissionValid() {
        return true;
    }

}
