package pathfinder.entities;

import api.pathfinder.SearchTarget;

/**
 * this class is used in the as item in the frontier and in the explored list at A* algorithm.
 * 
 * @author kaeserst, kustl1
 * 
 */
public class Node implements Comparable<Node> {

    /**
     * current SearchTarget
     */
    private SearchTarget state;
    /**
     * parent node
     */
    private Node parent;
    /**
     * the estimated cost from start to the target node.
     */
    private double costEstimated;
    /**
     * the path cost until this node.
     */
    private int actualCost;

    public Node(SearchTarget state, Node parent, int actualCosts, double costEstimated) {
        this.state = state;
        this.parent = parent;
        this.actualCost = actualCosts;
        this.costEstimated = actualCost + costEstimated;
    }

    /**
     * 
     * @return the position of the tile
     */
    public SearchTarget getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public int getActualCost() {
        return actualCost;
    }

    public double getEstimatedCost() {
        return costEstimated;
    }

    @Override
    public String toString() {
        return "Node [state=" + state + ", actual cost=" + actualCost + ", estimated cost= " + costEstimated + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        double result = 1;
        result = prime * result + actualCost + costEstimated;
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return (int) result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

    @Override
    /**
     * consider the digit behind the comma.
     */
    public int compareTo(Node o) {
        return ((int) costEstimated * 100) - ((int) o.costEstimated * 100);
    }
}
