package pathfinder.entities;

import api.SearchTarget;


/***
 * this class is used in the as item in the frontier and in the explored list at A* algorithm.
 * @author kaeserst
 *
 */
public class Node implements Comparable<Node> {

    /***
     * current SearchTarget
     */
    private SearchTarget state;
    /***
     * parent node
     */
    private Node parent;
    /***
     * the estimated cost from start to the target node.
     */
    private double costEstimated;
    /***
     * the path cost until this node.
     */
    private int costActual;

    public Node(SearchTarget state, Node parent, int costActual, double costEstimated) {
        this.state = state;
        this.parent = parent;
        this.costActual = costActual+state.getCost();
        this.costEstimated = costActual+costEstimated;
    }

    /***
     * 
     * @return the position of the tile
     */
    public SearchTarget getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public int getAcutalCost() {
        return costActual;
    }
    
    public double getEstimatedCost() {
        return costEstimated;
    }

    @Override
    public String toString() {
        return "Node [state=" + state + ", actual cost=" + costActual + ", estimated cost= "+costEstimated+"]";
        //return "Node [state=" + state + ", parent=" + (parent != null ? parent.state : "null") + ", cost=" + cost + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        double result = 1;
        result = prime * result + costActual +costEstimated;
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return (int)result;
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
        return ((int)costEstimated*100) - ((int)o.costEstimated*100);
    }

    public int getActualCost() {   
        return this.costActual;
    }
}
