package ants.search;

import ants.entities.SearchTarget;

public class Node implements Comparable<Node> {

    private SearchTarget state;
    private Node parent;
    private int costEstimated;
    private int costActual;

    public Node(SearchTarget state, Node parent, int costActual, int costEstimated) {
        this.state = state;
        this.parent = parent;
        this.costActual = costActual;
        this.costEstimated = costEstimated;
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
    
    public int getEstimatedCost() {
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
        int result = 1;
        result = prime * result + costActual +costEstimated;
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
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
        /*
         * if (cost != other.cost) return false; if (parent == null) { if (other.parent != null) return false; } else if
         * (!parent.equals(other.parent)) return false;
         */
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

    @Override
    public int compareTo(Node o) {
        return costEstimated - o.costEstimated;
    }

    public int getActualCost() {   
        return this.costActual;
    }
}
