package ants.entities;

import java.util.ArrayList;
import java.util.List;

import ants.search.Cluster;
import ants.search.PathFinder;

/***
 * DirectedEdge is used to define which is the start and the end node of the Edge
 * @author kaeserst
 *
 */
public class DirectedEdge extends Edge implements SearchTarget {

    private Tile startTile;

    public DirectedEdge(Tile vertices, Tile lastVertices, Cluster c) {
        super(vertices, lastVertices, c, null);
        startTile = vertices;
    }

    /**
     *change direction. start is end and vice versa
     */
    public void reverseEdge() {
        if (startTile == getTile1())
            startTile = getTile2();
        else
            startTile =  getTile1();
    }

/***
 * returns all successor edges of the current edges end node
 */
    @Override
    public List<SearchTarget> getSuccessors() {
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if(getCluster() == null)
            return list;
        list.addAll(getCluster().getEdgeWithNeighbourCluster(this));
        return list;
    }

    @Override
    public boolean isSearchable(boolean bParentNode) {
        return true;
    }

    @Override
    public int manhattanDistanceTo(SearchTarget dest) {
        return getEnd().manhattanDistanceTo(dest.getTargetTile());
    }
    
    /***
     * returns the path between the two tiles, if the path is not known it is calculated.
     */
    @Override
    public List<Tile> getPath() {
        if (path != null)
            return path;
        path = PathFinder.bestPath(PathFinder.A_STAR, getEnd(), getStart());
        return path;
    }

    /***
     * 
     * @return the end node of this edge
     */
    public Tile getEnd() {
        return  getTile1().equals(startTile) ?  getTile2() :  getTile1();
    }

    /***
     * 
     * @return  the start node of this edge
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
        return other.getEnd().equals(getEnd());
        
    }

    @Override
    public String toShortString() {
        return getStart() + "-" + getEnd();
    }

    @Override
    public boolean isFinal(SearchTarget to) {
        return getEnd().equals(to.getTargetTile()) || getStart().equals(to.getTargetTile());
    }

    @Override
    public int getCost() {
        if (getPath() != null)
            return getPath().size();

        return Math.abs(getStart().getRow() - getEnd().getRow()) + Math.abs(getStart().getCol() - getEnd().getCol());

    }

    @Override
    public double beelineTo(SearchTarget dest) {     
        return getEnd().beelineTo(dest.getTargetTile());
    }

}
