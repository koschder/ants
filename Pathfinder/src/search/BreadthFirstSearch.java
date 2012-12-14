package search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableMap;

public class BreadthFirstSearch {
    protected static final Logger LOGGER = LoggerFactory.getLogger(pathfinder.LogCategory.BFS);

    private SearchableMap map;

    public BreadthFirstSearch(SearchableMap map) {
        this.map = map;
    }

    public List<Tile> floodFill(Tile center, int maxDistance2) {
        return floodFill(center, maxDistance2, new AlwaysTrueGoalTest());
    }

    public List<Tile> floodFill(Tile center, int maxDistance2, GoalTest goalTest) {
        return findClosestTiles(center, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, goalTest);
    }

    public Tile findSingleClosestTile(Tile origin, int maxNodes, GoalTest goalTest) {
        List<Tile> found = findClosestTiles(origin, 1, maxNodes, Integer.MAX_VALUE, goalTest);
        return found.isEmpty() ? null : found.get(0);
    }

    public List<Tile> findClosestTiles(Tile origin, int numberOfHits, int maxNodes, int maxDistance2, GoalTest goalTest) {
        LOGGER.debug("BFS params: origin=%s, numberOfHits=%s, maxNodes=%s, maxDistance=%s", origin, numberOfHits,
                maxNodes, maxDistance2);
        LinkedList<Tile> frontier = new LinkedList<Tile>();
        frontier.add(origin);
        List<Tile> explored = new ArrayList<Tile>();
        List<Tile> found = new ArrayList<Tile>();
        while (!frontier.isEmpty() && explored.size() < maxNodes) {
            Tile next = frontier.poll();
            explored.add(next);
            List<SearchTarget> tiles = getSuccessors(next);
            for (SearchTarget child : tiles) {
                final Tile childTile = child.getTargetTile();
                // don't consider a tile twice
                if (frontier.contains(childTile) || explored.contains(childTile)) {
                    continue;
                }
                // check abort conditions
                if (maxDistance2 < Integer.MAX_VALUE && map.getSquaredDistance(origin, childTile) > maxDistance2) {
                    LOGGER.debug("MaxDistance (%s) exceeded.", maxDistance2);
                    return found;
                }
                if (found.size() >= numberOfHits) {
                    LOGGER.debug("Found enough Tiles: %s", numberOfHits);
                    return found;
                }

                // add child to frontier
                frontier.add(childTile);
                // add child to found if it is a goal
                if (goalTest.isGoal(childTile))
                    found.add(childTile);
            }
        }
        LOGGER.debug("Searched from %s, found %s, explored %s", origin, found, explored.size());
        return found;
    }

    private List<SearchTarget> getSuccessors(Tile next) {
        return map.getSuccessorsForSearch(next, false);
    }

    public static interface GoalTest {
        boolean isGoal(Tile tile);
    }

    public static class AlwaysTrueGoalTest implements GoalTest {
        @Override
        public boolean isGoal(Tile tile) {
            return true;
        }
    }
}
