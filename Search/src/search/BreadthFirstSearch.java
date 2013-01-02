package search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import api.entities.Tile;
import api.search.PathPiece;
import api.search.SearchableMap;

/**
 * this class implements the BFS (BreadthFirstSearch) search
 * 
 * @author kases1, kustl1
 * 
 */
public class BreadthFirstSearch {
    protected static final Logger LOGGER = LoggerFactory.getLogger(pathfinder.LogCategory.BFS);

    private SearchableMap map;

    /**
     * constructor with map on which the bfs is applied.
     * 
     * @param map
     */
    public BreadthFirstSearch(SearchableMap map) {
        this.map = map;
    }

    /**
     * returning all diagonal neighbors of the Tile center
     * 
     * @param center
     * @return
     */
    public List<Tile> getDiagonalNeighbours(final Tile center) {
        return floodFill(center, 4, new GoalTest() {
            @Override
            public boolean isGoal(Tile tile) {
                return map.manhattanDistance(center, tile) == 2 && map.getDirections(center, tile).size() == 2;
            }
        });
    }

    /**
     * returns all tiles in a radius of the squared distance maxDistance. the bfs is starting at center
     * 
     * @param center
     * @param maxDistance2
     * @return
     */
    public List<Tile> floodFill(Tile center, int maxDistance2) {
        return floodFill(center, maxDistance2, new AlwaysTrueGoalTest());
    }

    /**
     * returns all tiles in a radius of the squared distance maxDistance and the goalTest function. the bfs is starting
     * at center
     * 
     * @param center
     * @param maxDistance2
     * @param goalTest
     * @return
     */
    public List<Tile> floodFill(Tile center, int maxDistance2, GoalTest goalTest) {
        return findClosestTiles(center, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, goalTest);
    }

    /**
     * returns all tiles in a radius of the squared distance maxDistance and the goalTest function and a maximum node
     * restriction. the bfs is starting at center
     * 
     * @param center
     * @param maxNodes
     * @param goalTest
     * @return
     */
    public Tile findSingleClosestTile(Tile center, int maxNodes, GoalTest goalTest) {
        List<Tile> found = findClosestTiles(center, 1, maxNodes, Integer.MAX_VALUE, goalTest);
        return found.isEmpty() ? null : found.get(0);
    }

    /**
     * returns all tiles in a radius of the squared distance maxDistance and the goalTest function and a maximum node
     * restriction. the bfs is starting at center
     * 
     * @param center
     * @param numberOfHits
     * @param maxNodes
     * @param maxDistance2
     * @param goalTest
     * @return
     */
    public List<Tile> findClosestTiles(Tile center, int numberOfHits, int maxNodes, int maxDistance2, GoalTest goalTest) {
        return findClosestTiles(center, numberOfHits, maxNodes, maxDistance2, goalTest, new AlwaysFrontierTrueTest());
    }

    /**
     * returns all tiles in a radius of the squared distance maxDistance and the goalTest function, a maximum node
     * restriction and a numberOf hits restriction. the bfs is starting at center
     * 
     * @param center
     * @param numberOfHits
     * @param maxNodes
     * @param maxDistance2
     * @param goalTest
     * @return
     */
    public List<Tile> findClosestTiles(Tile center, int numberOfHits, int maxNodes, int maxDistance2,
            GoalTest goalTest, FrontierTest frontierTest) {
        LOGGER.debug("BFS params: origin=%s, numberOfHits=%s, maxNodes=%s, maxDistance=%s", center, numberOfHits,
                maxNodes, maxDistance2);
        LinkedList<Tile> frontier = new LinkedList<Tile>();
        frontier.add(center);
        List<Tile> explored = new ArrayList<Tile>();
        List<Tile> found = new ArrayList<Tile>();
        while (!frontier.isEmpty() && explored.size() < maxNodes) {
            Tile next = frontier.poll();
            explored.add(next);
            List<PathPiece> tiles = getSuccessors(next);
            for (PathPiece child : tiles) {
                final Tile childTile = child.getTargetTile();
                // don't consider a tile twice
                if (frontier.contains(childTile) || explored.contains(childTile)) {
                    continue;
                }
                // check abort conditions
                if (maxDistance2 < Integer.MAX_VALUE && map.getSquaredDistance(center, childTile) > maxDistance2) {
                    LOGGER.debug("MaxDistance (%s) exceeded.", maxDistance2);
                    return found;
                }
                if (found.size() >= numberOfHits) {
                    LOGGER.debug("Found enough Tiles: %s", numberOfHits);
                    return found;
                }

                // add child to frontier
                if (frontierTest.isFrontier(childTile))
                    frontier.add(childTile);
                // add child to found if it is a goal
                if (goalTest.isGoal(childTile))
                    found.add(childTile);
            }
        }
        LOGGER.debug("Searched from %s, found %s, explored %s", center, found, explored.size());
        return found;
    }

    /**
     * returns all neighbor tiles of the Tile next
     * 
     * @param next
     * @return
     */
    private List<PathPiece> getSuccessors(Tile next) {
        return map.getSuccessorsForSearch(next, false);
    }

    public static interface GoalTest {
        boolean isGoal(Tile tile);
    }

    public static interface FrontierTest {
        boolean isFrontier(Tile tile);
    }

    /**
     * this is a dummy implementation for the GoalTest it returns true for every Tile
     * 
     * @author kases1, kustl1
     * 
     */
    public static class AlwaysTrueGoalTest implements GoalTest {
        @Override
        public boolean isGoal(Tile tile) {
            return true;
        }
    }

    /**
     * this is a dummy implementation for the FrontierTest it returns true for every Tile
     * 
     * @author kases1, kustl1
     * 
     */
    public static class AlwaysFrontierTrueTest implements FrontierTest {
        @Override
        public boolean isFrontier(Tile tile) {
            return true;
        }
    }
}
