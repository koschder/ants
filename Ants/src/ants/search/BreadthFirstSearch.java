package ants.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

public class BreadthFirstSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.BFS);

    public Ant findMyClosestUnemployedAnt(Tile tile, int maxNodes) {
        Tile found = findSingleClosestTile(tile, maxNodes, new GoalTest() {
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
        });
        if (found != null)
            return Ants.getPopulation().getMyAntAt(found, true);
        return null;
    }

    public Tile findSingleClosestTile(Tile origin, int maxNodes, GoalTest goalTest) {
        List<Tile> found = findClosestTiles(origin, 1, maxNodes, goalTest);
        return found.isEmpty() ? null : found.get(0);
    }

    public List<Tile> findClosestTiles(Tile origin, int numberOfHits, int maxNodes, GoalTest goalTest) {
        LinkedList<Tile> frontier = new LinkedList<Tile>();
        frontier.add(origin);
        List<Tile> explored = new ArrayList<Tile>();
        List<Tile> found = new ArrayList<Tile>();
        while (!frontier.isEmpty() && explored.size() < maxNodes && found.size() < numberOfHits) {
            Tile next = frontier.poll();
            explored.add(next);
            List<SearchTarget> tiles = getSuccessors(next);
            for (SearchTarget child : tiles) {
                final Tile childTile = child.getTargetTile();
                if (frontier.contains(childTile) || explored.contains(childTile)) {
                    continue;
                }
                if (goalTest.isGoal(childTile)) {
                    found.add(childTile);
                } else {
                    frontier.add(childTile);
                }
            }
        }
        LOGGER.debug("Searched from %s, found %s, explored %s", origin, found, explored.size());
        return found;
    }

    protected List<SearchTarget> getSuccessors(Tile next) {
        return Ants.getWorld().getSuccessors(next, false, true);
    }

    public static interface GoalTest {
        boolean isGoal(Tile tile);
    }

}
