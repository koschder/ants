package pathfinder.entities;

import java.util.List;

import api.Tile;

/***
 * Describes a edge consists of to tiles as start and end node.
 * 
 * @author kases1,kustl1
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

    private Tile v1;
    private Tile v2;
    private Cluster cluster;
    private EdgeType type;

    /***
     * path between the tiles
     */
    protected List<Tile> path;

    public Edge(Tile vertices, Tile lastVertices, Cluster c) {
        this(vertices, lastVertices, c, null);
    }

    public Edge(Tile vertices, Tile lastVertices, Cluster c, EdgeType et) {
        v1 = vertices;
        v2 = lastVertices;
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
        v1 = path.get(0);
        v2 = path.get(path.size() - 1);
        this.setPath(path);
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

    public Cluster getCluster() {
        return cluster;
    }

    public List<Tile> getPath() {
        return path;
    }

    public Tile getTile1() {
        return v1;
    }

    public Tile getTile2() {
        return v2;
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
        return v1 + "-" + v2 + " " + type;
    }
}
