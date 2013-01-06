package ants.search;

import java.util.ArrayList;
import java.util.List;

import search.BreadthFirstSearch;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;
import api.search.SearchableMap;

/**
 * This class implements Ants-specific methods utilizing BreadthFirstSearch.
 * 
 * @author kases1, kustl1
 * 
 */
public class AntsBreadthFirstSearch extends BreadthFirstSearch {

    /**
     * Default constructor
     * 
     * @param map
     */
    public AntsBreadthFirstSearch(SearchableMap map) {
        super(map);
    }

    /**
     * Finds all enemies within the given distance from the origin
     * 
     * @param origin
     * @param maxDistance2
     * @return the tiles of the enemies
     */
    public List<Tile> findEnemiesInRadius(Tile origin, int maxDistance2) {
        List<Tile> tiles = findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasEnemyAnt();
            }
        });
        if (Ants.getWorld().getIlk(origin).hasEnemyAnt())
            tiles.add(origin);
        return tiles;
    }

    /**
     * Finds all friends within the given distance from the origin
     * 
     * @param origin
     * @param maxDistance2
     * @return the tiles of the friends
     */
    public List<Tile> findFriendsInRadius(Tile origin, int maxDistance2) {
        return findClosestTiles(origin, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return Ants.getWorld().getIlk(tile).hasFriendlyAnt();
            }
        });
    }

    /**
     * Finds all unemployed ants within distance, but not more than maxAnts
     * 
     * @param origin
     * @param maxAnts
     * @param maxManhattanDistance
     * @return the ants
     */
    public List<Ant> findUnemployedAntsInRadius(Tile origin, int maxAnts, int maxManhattanDistance) {
        // TODO watch this: without the cap for maxNodes, this search might run long; add a cap if it becomes a problem
        List<Tile> unemployedAntsTiles = findClosestTiles(origin, maxAnts, Integer.MAX_VALUE, maxManhattanDistance
                * maxManhattanDistance, new UnemployedAntGoalTest());
        return getAntsAt(unemployedAntsTiles);
    }

    /**
     * Finds the closest unemployed ants, searching at most maxNodes
     * 
     * @param tile
     * @param maxNodes
     * @return the ant, or null if none is found
     */
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

    /**
     * GoalTest implementation that check whether a given tile contains an unemployed ant
     */
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
