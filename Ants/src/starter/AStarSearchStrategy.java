package starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import starter.Logger.LogCategory;

public class AStarSearchStrategy implements SearchStrategy {

    private int MAXCOSTS = 6;

    public AStarSearchStrategy(int maxcosts) {
        MAXCOSTS = maxcosts;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see starter.SearchStrategy#bestPath(starter.Tile, starter.Tile)
     */
    public List<Tile> bestPath(Tile from, Tile to) {

        List<Tile> list = calculateBestPath(from, to);
        if (list != null && list.size() > 1) {
            Logger.debug(LogCategory.PATHFINDING, "list size() %s", list.size());
            list.remove(0); // first path-tile is position of ant (not the next step)
        }
        String length = list != null ? "has size of " + list.size() : "not found";
        Logger.debug(LogCategory.PATHFINDING, "Astar from: %s to %s best path size: %s path: %s", from, to, length,
                list);
        return list;
    }

    private List<Tile> calculateBestPath(Tile from, Tile to) {
        if (from.equals(to))
            return Arrays.asList(to);

        Set<Tile> explored = new HashSet<Tile>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(from, null, 0));
        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            explored.add(node.getState());
            Logger.debug(LogCategory.PATHFINDING, "Astar: %s exand(next) %s", node, expand(node));
            for (Node child : expand(node)) {
                if (frontier.contains(child) || explored.contains(child.getState()) || child.getCost() > MAXCOSTS)
                    continue;
                if (child.getState().equals(to))
                    return path(child); // success
                frontier.add(child);
                Logger.debug(LogCategory.PATHFINDING, "Astar frontier size: %s", frontier.size());
                Logger.debug(LogCategory.PATHFINDING, "Astar explored size: %s", explored.size());
            }
        }
        return null; // failure
    }

    public List<Node> expand(Node node) {
        List<Node> children = new ArrayList<Node>();
        final Tile north = Ants.getAnts().getTile(node.getState(), Aim.NORTH);
        final Tile south = Ants.getAnts().getTile(node.getState(), Aim.SOUTH);
        final Tile west = Ants.getAnts().getTile(node.getState(), Aim.WEST);
        final Tile east = Ants.getAnts().getTile(node.getState(), Aim.EAST);
        addChild(node, children, north);
        addChild(node, children, south);
        addChild(node, children, west);
        addChild(node, children, east);
        Logger.debug(LogCategory.PATHFINDING, "size of children %s", children.size());
        return children;
    }

    private void addChild(Node parent, List<Node> children, final Tile childState) {
        if (Ants.getAnts().getIlk(childState).isPassable() && !isOccupiedForNextMove(childState, parent)) {
            children.add(new Node(childState, parent, getCost(parent, childState)));
        } else {
            Logger.debug(LogCategory.PATHFINDING, "tile %s is not passable", childState);
        }
    }

    private boolean isOccupiedForNextMove(Tile childState, Node parent) {
        if (parent.getParent() == null) // we are on the 2nd level of the search tree
            return Ants.getAnts().getOrders().containsValue(childState);
        return false;
    }

    private int getCost(Node current, Tile dest) {
        return current.getCost() + current.getState().manhattanDistanceTo(dest);
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
        path.add(child.getState());
        addToPath(path, child.getParent());
    }
}
