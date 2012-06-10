package ants.entities;

import java.util.List;

import ants.search.Cluster;


public class Edge {

    public enum EdgeType { North, South, East, West, Intra };
    private Tile v1;
    private Tile v2;
    private Cluster cluster;
    private EdgeType type;

    public EdgeType getType() {
        return type;
    }

    // if no path defined the tiles can reached staright
    protected List<Tile> path;

    public Edge(Tile vertices, Tile lastVertices, Cluster c){
        this(vertices,lastVertices,c,null);
    }
    
    public Edge(Tile vertices, Tile lastVertices, Cluster c,EdgeType et) {
        v1 = vertices;
        v2 = lastVertices;
        cluster = c;
        type = et;
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

    public void setCluster(Cluster c) {
        cluster = c;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setEdgeType(EdgeType et) {
       this.type = et;        
    }

    public Tile getTile1() {
        return v1;
    }
    
    public Tile getTile2() {
        return v2;
    }
}
