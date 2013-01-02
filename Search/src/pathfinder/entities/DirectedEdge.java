package pathfinder.entities;

import java.util.Collections;
import java.util.List;

import api.entities.Tile;
import api.search.PathPiece;

/**
 * DirectedEdge extended of Edge and is used to define which is the start and the end tile of the Edge
 * 
 * @author kases1, kustl1
 * 
 */
public class DirectedEdge extends Edge implements PathPiece {

    private Tile startTile;

    public DirectedEdge(Tile vertices, Tile lastVertices, Cluster c) {
        super(vertices, lastVertices, c, null);
        startTile = vertices;
    }

    public DirectedEdge(Tile vertices, Tile lastVertices, Cluster c, List<Tile> path) {
        super(vertices, lastVertices, c, null);
        startTile = vertices;
        setPath(path);
    }

    /**
     * change direction. start is end and vice versa
     */
    public void reverseEdge() {
        if (startTile == getTile1())
            startTile = getTile2();
        else
            startTile = getTile1();
    }

    /**
     * returns the path between the two tiles, if the path is not known it is calculated.
     */
    @Override
    public List<Tile> getPath() {
        if (path == null)
            throw new RuntimeException("path is null, it must be calculated before instanced the object");

        if (path.get(0).equals(getEnd())) {
            Collections.reverse(path);
            return path;
        } else {
            return path;
        }
    }

    /**
     * 
     * @return the end node of this edge
     */
    public Tile getEnd() {
        return getTile1().equals(startTile) ? getTile2() : getTile1();
    }

    /**
     * 
     * @return the start node of this edge
     */
    public Tile getStart() {
        return this.startTile;
    }

    @Override
    public boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2) {
        return true;
    }

    @Override
    public Tile getTargetTile() {
        return getStart();
    }

    @Override
    public String toString() {
        return super.toString() + " " + getCluster();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DirectedEdge other = (DirectedEdge) obj;
        return other.getEnd().equals(getEnd()) && other.getStart().equals(getStart());

    }

    @Override
    public String toShortString() {
        return getStart() + "-" + getEnd();
    }

    @Override
    public boolean isFinal(PathPiece to) {
        return getEnd().equals(to.getTargetTile()) || getStart().equals(to.getTargetTile());
    }

    @Override
    public int getCost() {
        if (getPath() != null)
            return getPath().size();

        return Math.abs(getStart().getRow() - getEnd().getRow()) + Math.abs(getStart().getCol() - getEnd().getCol());

    }

}
