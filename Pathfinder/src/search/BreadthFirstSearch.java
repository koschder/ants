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
        return findClosestTiles(center, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance2, new AlwaysTrueGoalTest());
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
        while (!frontier.isEmpty() && explored.size() < maxNodes && found.size() < numberOfHits) {
            Tile next = frontier.poll();
            if (maxDistance2 < Integer.MAX_VALUE && maxDistance2 < map.getSquaredDistance(origin, next)) {
                LOGGER.debug("MaxDistance (%s) exceeded.", maxDistance2);
                break;
            }
            explored.add(next);
            List<SearchTarget> tiles = getSuccessors(next);
            for (SearchTarget child : tiles) {
                final Tile childTile = child.getTargetTile();
                if (frontier.contains(childTile) || explored.contains(childTile)) {
                    continue;
                }
                frontier.add(childTile);
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
