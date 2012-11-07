package ants.missions;

import java.util.*;

import pathfinder.*;
import ants.entities.*;
import ants.state.*;
import api.entities.*;

public class FlockMission extends BaseMission {
    public FlockMission(Ant ant) {
        super(ant);
    }

    private List<Ant> ants;
    private Tile target;

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void execute() {
        sortAnts();
        Ant leader = ants.get(0);
        List<Tile> path = Ants.getPathFinder().search(PathFinder.Strategy.AStar, leader.getTile(), target);
        final Tile virtualTarget = path.get(0);
        if (!putMissionOrder(leader, virtualTarget)) {
            abandonMission(); // no path to target
            return;
        }
        for (Ant ant : ants) {
            if (ant.equals(leader))
                continue;
            List<Tile> virtualPath = Ants.getPathFinder().search(PathFinder.Strategy.Simple, ant.getTile(),
                    virtualTarget);
            if (virtualPath != null && putMissionOrder(ant, virtualPath.get(0)))
                continue;
            else {
                // TODO 8 neighbours
                List<Tile> neighbours = Ants.getWorld().get4Neighbours(virtualTarget);
                for (Tile neighbour : neighbours) {
                    virtualPath = Ants.getPathFinder().search(PathFinder.Strategy.Simple, ant.getTile(), neighbour);
                    if (virtualPath != null && putMissionOrder(ant, virtualPath.get(0)))
                        break;
                }
                // TODO add more tiles to the possible targets
            }
            // if an ant can't make a sensible move this turn, just mark it as employed
            Ants.getPopulation().addEmployedAnt(ant);
        }

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
