package ants.entities;

import java.util.ArrayList;
import java.util.List;

/***
 * Describes a node in a search path and stores the sucessor edges.
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
        edges.add(e);
    }

}
