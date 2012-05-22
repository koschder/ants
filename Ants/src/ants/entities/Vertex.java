package ants.entities;

import java.util.ArrayList;
import java.util.List;


public class Vertex extends Tile {

    public List<Edge> edges = new ArrayList<Edge>();

    public Vertex(Tile t, Edge e) {
        super(t.getRow(), t.getCol());
        edges.add(e);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

}
