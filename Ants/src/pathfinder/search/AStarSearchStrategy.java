package pathfinder.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import pathfinder.PathFinder;
import pathfinder.entities.Node;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.Tile;

import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class AStarSearchStrategy extends SearchStrategy {

    public AStarSearchStrategy(PathFinder f) {
        super(f);
    }


    private SearchTarget to;

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see starter.SearchStrategy#bestPath(starter.Tile, starter.Tile)
     */
    public List<Tile> search(SearchTarget from, SearchTarget to) {
        this.to = to;
        Logger.debug(LogCategory.PATHFINDING, "**************** Astar_start: %s to %s", from.toShortString(),
                to.toShortString());
        if (searchSpace1 != null) {
            Logger.debug(LogCategory.PATHFINDING, "In searchspace: %s to %s", searchSpace1, searchSpace2);
        }
        long start = System.currentTimeMillis();
        List<Tile> list = calculateBestPath(from, to);
        if (list != null && list.size() > 1) {
            Logger.debug(LogCategory.PATHFINDING, "list size() %s", list.size());
            list.remove(0); // first path-tile is position of ant (not the next step)
        }
        String length = list != null ? "has size of " + list.size() : "not found";
        long elapsed = System.currentTimeMillis() - start;
        Logger.debug(LogCategory.PATHFINDING,
                "****************  Astar_end:  best path size: %s from: %s to %s duration %s path: %s", length, from,
                to, elapsed, list);
        return list;
    }

    private List<Tile> calculateBestPath(SearchTarget from, SearchTarget to) {
        if (from.equals(to))
            return to.getPath();

        Set<SearchTarget> explored = new HashSet<SearchTarget>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(from, null, 0, from.beelineTo(to)));
        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            explored.add(node.getState());

            List<Node> nodes = expand(node);
            for (Node child : nodes) {
                if (frontier.contains(child) || explored.contains(child.getState()) || maxCostReached(child, to)) {
                    continue;
                }
                if (child.getState().isFinal(to))
                    return path(child); // success
                frontier.add(child);
            }
        }
        return null; // failure
    }

    private boolean maxCostReached(Node child, SearchTarget to) {
        return child.getEstimatedCost() > maxCost && maxCost != -1;
    }

    public List<Node> expand(Node node) {
        List<Node> children = new ArrayList<Node>();
        List<SearchTarget> list = pathFinder.getSuccessor(node.getState(), node.getParent() == null);
        for (SearchTarget a : list) {
            addChild(node, children, a);
        }
        return children;
    }

    private void addChild(Node parent, List<Node> children, SearchTarget childState) {
        if (!inSearchSpace(childState)) {
            Logger.debug(LogCategory.PATHFINDING, "tile %s is not in searchspace", childState);
            return;
        }
        children.add(new Node(childState, parent, getActualCost(parent, childState), childState.beelineTo(to)));

    }

    private int getActualCost(Node current, SearchTarget dest) {

        return current.getActualCost();
    }

    private List<Tile> path(Node child) {
        List<Tile> path = new ArrayList<Tile>();
        addToPath(path, child);
        Collections.reverse(path);
        return path;
    }

    private void addToPath(List<Tile> path, Node child) {
        if (child == null)
            return;
        path.addAll(child.getState().getPath());
        addToPath(path, child.getParent());
    }


}
