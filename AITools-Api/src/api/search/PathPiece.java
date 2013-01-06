package api.search;

import java.util.List;

import api.entities.Tile;

/**
 * This interface provides Tiles or Edges to be used in A* and HPA*
 * 
 * @author kases1, kustl1
 * 
 */
public interface PathPiece {

    /**
     * 
     * @return the path by which this PathPiece can be reached
     */
    List<Tile> getPath();

    /**
     * 
     * @return the tile this SearchTarget leads to.
     */
    Tile getTargetTile();

    /**
     * 
     * @return a shorter version of toString()
     */
    String toShortString();

    /**
     * 
     * @param to
     * @return True if the PathPiece is the final state (target) of the search.
     */
    boolean isFinal(PathPiece to);

    /**
     * 
     * @return cost of this path piece
     */
    int getCost();

}
