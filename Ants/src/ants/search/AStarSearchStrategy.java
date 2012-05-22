package ants.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import ants.entities.SearchTarget;
import ants.entities.Tile;
import ants.util.Logger;
import ants.util.Logger.LogCategory;

public class AStarSearchStrategy implements SearchStrategy {

    private int MAXCOSTS = 6;
    private Tile searchSpace1;
    private Tile searchSpace2;

    public AStarSearchStrategy(int maxcosts) {
        MAXCOSTS = maxcosts;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see starter.SearchStrategy#bestPath(starter.Tile, starter.Tile)
     */
    public List<Tile> bestPath(SearchTarget from, SearchTarget to) {
        Logger.debug(LogCategory.PATHFINDING, "**************** Astar_start: %s to %s", from.toShortString(),
                to.toShortString());
        if (searchSpace1 != null) {
            Logger.debug(LogCategory.PATHFINDING, "In searchspace: %s to %s", searchSpace1, searchSpace2);
        }
        List<Tile> list = calculateBestPath(from, to);
        if (list != null && list.size() > 1) {
            Logger.debug(LogCategory.PATHFINDING, "list size() %s", list.size());
            list.remove(0); // first path-tile is position of ant (not the next step)
        }
        String length = list != null ? "has size of " + list.size() : "not found";
        Logger.debug(LogCategory.PATHFINDING,
                "****************  Astar_end:  best path size: %s from: %s to %s path: %s", length, from, to, list);
        return list;
    }

    private List<Tile> calculateBestPath(SearchTarget from, SearchTarget to) {
        if (from.equals(to))
            return to.getPath();

        Set<SearchTarget> explored = new HashSet<SearchTarget>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(from, null, 0));
        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            explored.add(node.getState());

            List<Node> nodes = expand(node);
            Logger.debug(LogCategory.PATHFINDING, "Astar: %s expanded(next) %s", node, nodes);
            for (Node child : nodes) {
                if (frontier.contains(child) || explored.contains(child.getState()) || maxCostReached(child, to)) {
                    Logger.debug(LogCategory.PATHFINDING, "Skip new node: %s", child);
                    continue;
                }
                if (child.getState().equals(to))
                    return path(child); // success
                frontier.add(child);
                Logger.debug(LogCategory.PATHFINDING, "Astar frontier size: %s", frontier.size());
                Logger.debug(LogCategory.PATHFINDING, "Astar explored size: %s", explored.size());
            }
        }
        return null; // failure
    }

    private boolean maxCostReached(Node child, SearchTarget to) {
        return child.getCost() + child.getState().distanceTo(to) > MAXCOSTS;
    }

    public List<Node> expand(Node node) {
        List<Node> children = new ArrayList<Node>();
        List<SearchTarget> list = node.getState().getSuccessors();
        for (SearchTarget a : list) {
            addChild(node, children, a);
        }
        // addChild(node, children, north);
        // addChild(node, children, south);
        // addChild(node, children, west);
        // addChild(node, children, east);
        Logger.debug(LogCategory.PATHFINDING, "size of children %s", children.size());
        return children;
    }

    private void addChild(Node parent, List<Node> children, SearchTarget childState) {
        if (!inSearchSpace(childState)) {
            Logger.debug(LogCategory.PATHFINDING, "tile %s is not in searchspace", childState);
            return;
        }

        if (childState.isSearchable((parent.getParent() == null))) {
            children.add(new Node(childState, parent, getCost(parent, childState)));
        } else {
            Logger.debug(LogCategory.PATHFINDING, "tile %s is not passable", childState);
        }
    }

    private boolean inSearchSpace(SearchTarget areaCheck) {
        return areaCheck.isInSearchSpace(searchSpace1, searchSpace2);
    }

    private int getCost(Node current, SearchTarget dest) {
        return current.getCost() + current.getState().distanceTo(dest);
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

    @Override
    public void setMaxCost(int i) {
        this.MAXCOSTS = i;
    }

    @Override
    public void setSearchSpace(Tile p1, Tile p2) {
        this.searchSpace1 = p1;
        this.searchSpace2 = p2;
    }
}
