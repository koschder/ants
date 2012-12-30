package pathfinder.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.entities.Node;
import api.entities.Tile;
import api.pathfinder.SearchTarget;

/**
 * this class implements the AStar algorithm for path finding
 * 
 * @author kases1, kustl1
 * 
 */
public class AStarSearchStrategy extends SearchStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.PATHFINDING);

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
    public List<Tile> searchPath(SearchTarget from, SearchTarget to) {
        this.to = to;
        List<Tile> list = calculateBestPath(from, to);
        return list;
    }

    /**
     * main method, calculates a Astar path with actual and estimated costs, using a frontier and an explored list.
     * 
     * @param from
     *            Start area
     * @param to
     *            Emd area
     * @return path if found, else null
     */
    private List<Tile> calculateBestPath(SearchTarget from, SearchTarget to) {
        if (from.equals(to))
            return to.getPath();
        boolean bPrintFrontier = false;
        Set<SearchTarget> explored = new HashSet<SearchTarget>();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(from, null, 0, getEstimatedCosts(from.getTargetTile(), to.getTargetTile())));
        while (!frontier.isEmpty()) {

            if (bPrintFrontier) {
                System.out.println("Frontier is:");
                for (Node n : frontier) {
                    System.out.println(n);
                }
            }

            Node node = frontier.poll();
            explored.add(node.getState());

            List<Node> nodes = expand(node);
            for (Node child : nodes) {
                if (frontier.contains(child) || explored.contains(child.getState()) || maxCostReached(child)) {
                    continue;
                }
                if (child.getState().isFinal(to)) {
                    return path(child);
                }
                frontier.add(child);
            }
        }
        return null; // failure
    }

    /**
     * are the maximum costs reached with this node..
     * 
     * @param node
     * @param to
     * @return
     */
    private boolean maxCostReached(Node node) {
        return node.getEstimatedCost() > maxCost && maxCost != -1;
    }

    /**
     * expanding the node by looking up its neighbor tiles.
     * 
     * @param node
     * @return the passable neighbors.
     */
    public List<Node> expand(Node node) {
        List<Node> children = new ArrayList<Node>();
        List<SearchTarget> list = pathFinder.getMap().getSuccessorsForPathfinding(node.getState(),
                node.getParent() == null);
        for (SearchTarget a : list) {
            addChild(node, children, a);
        }
        return children;
    }

    /**
     * adding all children to its parent node.
     * 
     * @param parent
     * @param children
     * @param childState
     */
    private void addChild(Node parent, List<Node> children, SearchTarget childState) {
        if (!inSearchSpace(childState)) {
            LOGGER.debug("tile %s is not in searchspace", childState);
            return;
        }
        int actualCosts = getActualCost(parent, childState);
        double estimatedCosts = getEstimatedCosts(childState.getTargetTile(), to.getTargetTile());
        children.add(new Node(childState, parent, actualCosts, estimatedCosts));

    }

    /**
     * returning the path from the child Node to the root node.
     * 
     * @param child
     * @return
     */
    private List<Tile> path(Node child) {
        List<Tile> path = new ArrayList<Tile>();
        addToPath(path, child);
        Collections.reverse(path);
        return path;
    }

    /**
     * adds the path of the child node to the actual path
     * 
     * @param path
     * @param child
     */
    private void addToPath(List<Tile> path, Node child) {
        if (child == null)
            return;
        List<Tile> newpath = child.getState().getPath();
        Collections.reverse(newpath);
        // patch together path sequences in case of HPA*
        if (path.size() > 0 && path.get(path.size() - 1).equals(newpath.get(0)))
            newpath = newpath.subList(1, newpath.size());
        path.addAll(newpath);
        addToPath(path, child.getParent());
    }

}
