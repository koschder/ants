package pathfinder;

import java.util.ArrayList;
import java.util.List;

import pathfinder.search.AStarSearchStrategy;
import pathfinder.search.SearchStrategy;
import pathfinder.search.SimpleSearchStrategy;
import api.entities.Tile;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableMap;
import api.strategy.InfluenceMap;

public class SimplePathFinder implements PathFinder {

    protected SearchableMap map;
    protected InfluenceMap infMap;

    public SimplePathFinder(SearchableMap map) {
        this.map = map;
    }

    public SimplePathFinder(SearchableMap map, InfluenceMap infMap) {
        this.map = map;
        this.infMap = infMap;
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, int maxCost) {
        SearchStrategy searchStrat = getStrategy(strategy);
        searchStrat.setMaxCost(maxCost);
        final List<Tile> path = searchStrat.search(start, end);
        if (path != null && path.size() > 0)
            validatePath(path);
        return path;
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end) {
        return search(strategy, start, end, -1);
    }

    @Override
    public List<Tile> search(Strategy strategy, SearchTarget start, SearchTarget end, Tile searchSpace0,
            Tile searchSpace1, int maxCost) {
        SearchStrategy searchStrat = getStrategy(strategy);
        searchStrat.setMaxCost(maxCost);
        searchStrat.setSearchSpace(searchSpace0, searchSpace1);
        return searchStrat.search(start, end);
    }

    /***
     * 
     * @return the map on witch the path finder acts.
     */
    @Override
    public SearchableMap getMap() {
        return this.map;
    }

    /***
     * 
     * @param strategy
     * @return the instance of the search strategy
     */
    protected SearchStrategy getStrategy(Strategy strategy) {
        if (strategy == Strategy.Simple)
            return new SimpleSearchStrategy(this);
        if (strategy == Strategy.AStar)
            return new AStarSearchStrategy(this);

        throw new RuntimeException("Strategy not implemented: " + strategy);
    }

    @Override
    public void update() {
        // nothing to do
    }

    public boolean validatePath(List<Tile> path) {
        boolean ret = true;
        if (path.isEmpty())
            ret = false;
        for (int i = 0; i < path.size() - 2; i++) {
            final Tile current = path.get(i);
            final Tile next = path.get(i + 1);
            if (!map.isPassable(next)) {
                System.out.println(String.format("Tile is not passable: %s ", next));
                ret = false;
            }
            int dist = map.manhattanDistance(current, next);
            if (dist != 1) {
                System.out.println(String.format("Invalid manhattanDistance between %s and %s ", current, next));
                ret = false;
            }
        }
        if (!ret)
            throw new IllegalStateException("Invalid path: " + path.toString());
        return ret;
    }

    public List<Tile> smoothPath(List<Tile> path) {
        return smoothPath(path, 10, false);
    }

    public List<Tile> smoothPath(List<Tile> path, int size, boolean recursive) {
        if (path == null || path.size() < size)
            return path;
        int start = 0;
        int current = size;
        List<Tile> newPath = new ArrayList<Tile>();
        // do while the last tile of path is new path
        do {

            List<Tile> subPath = path.subList(start, current);
            int manDist = map.manhattanDistance(subPath.get(0), subPath.get(subPath.size() - 1)) + 1;

            List<Tile> newSubPath = null;
            if (manDist < subPath.size()) {
                newSubPath = search(Strategy.AStar, subPath.get(0), subPath.get(subPath.size() - 1), subPath.size() - 1);
            }
            if (newSubPath != null) {
                // Collections.reverse(newSubPath); // todo why is here the path the other way round.
                newPath.addAll(newSubPath);
                if (recursive) {
                    newPath = smoothPath(newPath, newPath.size(), true);
                }
            } else {
                newPath.addAll(subPath);
            }
            start = current;
            current = Math.min(current + size, path.size());
        } while (!path.get(path.size() - 1).equals(newPath.get(newPath.size() - 1)));

        return newPath;
    }

    @Override
    public InfluenceMap getInfluenceMap() {
        return infMap;
    }

    public int getPathCosts(List<Tile> path) {
        int costs = 0;
        for (Tile t : path) {
            costs += (infMap == null) ? 1 : infMap.getPathCosts(t);
        }
        return costs;
    }

    public String getPathCostsString(List<Tile> path) {
        String costs = "";
        for (Tile t : path) {
            costs += (infMap == null) ? ",1" : "," + infMap.getPathCosts(t);
        }
        return costs.length() > 0 ? costs.substring(1) : "";
    }
}