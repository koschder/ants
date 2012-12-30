package pathfinder.entities;

import java.util.List;

import api.entities.Tile;

/**
 * Describes a edge consists of to tiles as start and end node.
 * 
 * @author kases1, kustl1
 * 
 */
public class Edge {

    public enum EdgeType {
        North,
        South,
        East,
        West,
        Intra
    };

    private Tile vertex1;
    private Tile vertex2;
    private Cluster cluster;
    private EdgeType type;

    /**
     * path between the tiles
     */
    protected List<Tile> path;

    public Edge(Tile vertices, Tile lastVertices, Cluster c) {
        this(vertices, lastVertices, c, null);
    }

    public Edge(Tile vertices, Tile lastVertices, Cluster c, EdgeType et) {
        vertex1 = vertices;
        vertex2 = lastVertices;
        cluster = c;
        type = et;
    }

    public Edge(Tile vertices, Tile lastVertices, List<Tile> newPath, Cluster c) {
        this(vertices, lastVertices, c);
        this.setPath(newPath);
    }

    public Edge(List<Tile> path) {
        if (path == null || path.size() < 2)
            throw new IllegalArgumentException("Path must have a size of at least 2");
        vertex1 = path.get(0);
        vertex2 = path.get(path.size() - 1);
        this.setPath(path);
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Edge) {
            Edge e = (Edge) o;
            result = vertex1.equals(e.vertex1) && vertex2.equals(e.vertex2);
            result |= vertex2.equals(e.vertex1) && vertex1.equals(e.vertex2);
        }
        return result;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public List<Tile> getPath() {
        return path;
    }

    public Tile getTile1() {
        return vertex1;
    }

    public Tile getTile2() {
        return vertex2;
    }

    public EdgeType getType() {
        return type;
    }

    /**
     * 
     * @return true if the path is already calculated.
     */
    public boolean hasPath() {
        return (path != null) && (path.size() > 0);
    }

    public void setCluster(Cluster c) {
        cluster = c;
    }

    public void setEdgeType(EdgeType et) {
        this.type = et;
    }

    public void setPath(List<Tile> newPath) {
        path = newPath;
    }

    @Override
    public String toString() {
        return vertex1 + "-" + vertex2 + " " + type;
    }
}
