package pathfinder.entities;

import java.util.ArrayList;
import java.util.List;

import api.entities.Tile;

/**
 * Describes a node in a search path and stores the successor edges.
 * 
 * @author kases1, kustl1
 * 
 */
public class Vertex extends Tile {

    private List<Edge> edges = new ArrayList<Edge>();

    public Vertex(Tile t, Edge e) {
        super(t.getRow(), t.getCol());
        if (e != null)
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
}
