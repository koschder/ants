package starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AStarSearchStrategy implements SearchStrategy {

    private Ants ants;
    private int MAXCOSTS = 6;

    public AStarSearchStrategy(Ants ants, int maxcosts) {
        this.ants = ants;
        MAXCOSTS = maxcosts;
    }

    @Override
    public List<Tile> bestPath(Tile from, Tile to) {

        Logger.log("Astar starting from: %s to %s", from, to);
        if (from.equals(to))
            return Arrays.asList(to);

        Set<Tile> explored = new HashSet<Tile>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(from, null, 0));
        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            explored.add(node.getState());
            // Logger.log("Astar: %s exand(next) %s", node,expand(node));
            for (Node child : expand(node)) {
                if (frontier.contains(child) || explored.contains(child.getState()) || child.getCost() > MAXCOSTS)
                    continue;
                if (child.getState().equals(to))
                    return path(child); // success
                frontier.add(child);
                // Logger.log("Astar frontier size: %s", frontier.size());
                // Logger.log("Astar explored size: %s", explored.size());
            }
        }
        return null; // failure
    }

    public SortedSet<Node> expand(Node node) {
        SortedSet<Node> children = new TreeSet<Node>();
        final Tile north = ants.getTile(node.getState(), Aim.NORTH);
        final Tile south = ants.getTile(node.getState(), Aim.SOUTH);
        final Tile west = ants.getTile(node.getState(), Aim.WEST);
        final Tile east = ants.getTile(node.getState(), Aim.EAST);
        addChild(node, children, north);
        addChild(node, children, south);
        addChild(node, children, west);
        addChild(node, children, east);
        // Logger.log("size of children %s",children.size());
        return children;
    }

    private void addChild(Node parent, SortedSet<Node> children, final Tile childState) {
        if (ants.getIlk(childState).isPassable() && !isOccupiedForNextMove(childState, parent)) {
            children.add(new Node(childState, parent, getCost(parent, childState)));
        } else {
            // Logger.log("tile %s is not passable",childState);
        }
    }

    private boolean isOccupiedForNextMove(Tile childState, Node parent) {
        if (parent.getParent() == null) // we are on the 2nd level of the search tree
            return ants.getOrders().containsValue(childState);
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
