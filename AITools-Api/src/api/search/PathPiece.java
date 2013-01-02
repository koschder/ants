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
     * Returns the path which this PathPiece overcomes
     * 
     * @return
     */
    List<Tile> getPath();

    /**
     * Checks if this PathPiece is in a search space given by the parameters.
     * 
     * @param searchSpace1
     * @param searchSpace2
     * @return
     */
    boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2);

    /**
     * returns the tile on wich this PathPiece leds to.
     * 
     * @return
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
