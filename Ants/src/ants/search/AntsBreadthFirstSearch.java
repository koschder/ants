package ants.search;

import java.util.ArrayList;
import java.util.List;

import search.BreadthFirstSearch;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;
import api.pathfinder.SearchableMap;

public class AntsBreadthFirstSearch extends BreadthFirstSearch {

    public AntsBreadthFirstSearch(SearchableMap map) {
        super(map);
    }

    public List<Tile> findEnemiesInRadius(Tile origin, int maxDistance2) {
        return findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasEnemyAnt();
            }
        });
    }

    public List<Tile> findFriendsInRadius(Tile origin, int maxDistance2) {
        return findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasFriendlyAnt();
            }
        });
    }

    public List<Ant> findUnemployedAntsInRadius(Tile origin, int maxAnts, int maxManhattanDistance) {
        // TODO watch this: without the cap for maxNodes, this search might run long; add a cap if it becomes a problem
        List<Tile> unemployedAntsTiles = findClosestTiles(origin, maxAnts, Integer.MAX_VALUE, maxManhattanDistance
                * maxManhattanDistance, new UnemployedAntGoalTest());
        return getAntsAt(unemployedAntsTiles);
    }

    public Ant findMyClosestUnemployedAnt(Tile tile, int maxNodes) {
        Tile found = findSingleClosestTile(tile, maxNodes, new UnemployedAntGoalTest());
        if (found != null)
            return Ants.getPopulation().getMyAntAt(found, true);
        return null;
    }

    private List<Ant> getAntsAt(List<Tile> tiles) {
        List<Ant> ants = new ArrayList<Ant>();
        for (Tile tile : tiles) {
            ants.add(Ants.getPopulation().getMyAntAt(tile, false));
        }
        return ants;
    }

    public static class UnemployedAntGoalTest implements GoalTest {
        @Override
        public boolean isGoal(Tile tile) {
            final boolean hasFriendlyAnt = Ants.getWorld().getIlk(tile).hasFriendlyAnt();
            if (hasFriendlyAnt) {
                if (Ants.getPopulation().getMyAntAt(tile, true) != null)
                    return true;
                else {
                    LOGGER.debug("%s has friendly ant, but is not unemployed", tile);
                }
            }
            return false;
        }
    }
}
