package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

import api.entities.Tile;

/***
 * Describes a node in a search path and stores the sucessor edges.
 * 
 * @author kases1, kustl1
 * 
 */
public class Vertex extends Tile {

    private List<Edge> edges = new ArrayList<Edge>();

    public Vertex(Tile t, Edge e) {
        super(t.getRow(), t.getCol());
        edges.add(e);
    }

    public void addEdge(Edge e) {
        if (!edges.contains(e))
            edges.add(e);
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            result = super.equals(o);
        }
        return result;
    }

    // public int compareTo(Vertex o) {
    // return hashCode() - hashCode();
    // }
    //
    // public int hashCode() {
    // return super.hashCode();
    // }

}
