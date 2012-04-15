package ants.search;

import ants.entities.Tile;

public class Node implements Comparable<Node> {

    private Tile state;
    private Node parent;
    private int cost;

    public Node(Tile state, Node parent, int cost) {
        this.state = state;
        this.parent = parent;
        this.cost = cost;
    }

    /***
     * 
     * @return the position of the tile
     */
    public Tile getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Node [state=" + state + ", parent=" + (parent != null ? parent.state : "null") + ", cost=" + cost + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cost;
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
        if (cost != other.cost)
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

    @Override
    public int compareTo(Node o) {
        return cost - o.cost;
    }
}
