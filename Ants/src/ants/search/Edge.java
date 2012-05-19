package ants.search;

import java.util.List;

import ants.entities.Tile;

public class Edge {

    public Tile v1;
    public Tile v2;

    // if no path defined the tiles can reached staright
    public List<Tile> path;

    public Edge(Tile vertices, Tile lastVertices) {
        v1 = vertices;
        v2 = lastVertices;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Edge) {
            Edge e = (Edge) o;
            result = v1.equals(e.v1) && v2.equals(e.v2);
            result |= v2.equals(e.v1) && v1.equals(e.v2);
        }
        return result;
    }

    public void setPath(List<Tile> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return v1 + "-" + v2;
    }

}
