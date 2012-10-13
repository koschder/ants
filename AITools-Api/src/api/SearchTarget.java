package api;

import java.util.List;

/***
 * This interface provides Tiles or Edges to be used in A* and HPA*
 * 
 * @author kases1, kustl1
 * 
 */
public interface SearchTarget {

    /***
     * 
     * @return the next SearchTarget(s) on were the path goes on.
     */
    // List<SearchTarget> getSuccessors();

    /***
     * Returns true if the SearchTarget can be used for path finding.
     * 
     * @param bParentNode
     * @return
     */
    // boolean isSearchable(boolean bParentNode);

    /***
     * Returns the path which this SearchTarget overcomes
     * 
     * @return
     */
    List<Tile> getPath();

    /***
     * Checks if this SearchTarget is in a search space given by the parameters.
     * 
     * @param searchSpace1
     * @param searchSpace2
     * @return
     */
    boolean isInSearchSpace(Tile searchSpace1, Tile searchSpace2);

    /***
     * returns the tile on wich this SearchTarget leds to.
     * 
     * @return
     */
    Tile getTargetTile();

    /***
     * 
     * @return a shorter version of toString()
     */
    String toShortString();

    /**
     * 
     * @param to
     * @return True if the SearchTarget is the final state (target) of the search.
     */
    boolean isFinal(SearchTarget to);

    /***
     * 
     * @return cost of this path piece
     */
    int getCost();

}
