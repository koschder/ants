package api.pathfinder;

import java.util.List;

import api.entities.Aim;
import api.entities.Tile;
import api.map.TileMap;
import api.strategy.InfluenceMap;

/**
 * the interface SearchableMap defines the method used doing path searching on the map.
 * 
 * @author kases1, kustl1
 * 
 */
public interface SearchableMap extends TileMap {

    /**
     * returns all neighbor fields, passable for any movable object
     * 
     * @param currentPosition
     *            actual position
     * @param isNextMove
     *            (is this path part used for next game step or later)
     * @return all "next" positions
     */
    public List<SearchTarget> getSuccessorsForPathfinding(SearchTarget currentPosition, boolean isNextMove);

    /**
     * Returns one or two orthogonal directions from one location to the another.
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * 
     * @return orthogonal directions from <code>t1</code> to <code>t2</code>
     */
    public List<Aim> getDirections(Tile t1, Tile t2);

    /**
     * Returns the principal direction tile t1 to t2 (i.e. if t2 is 3 steps to the north and 5 steps to the west, this
     * method will return WEST)
     * 
     * @param t1
     * @param t2
     * @return
     */
    public Aim getPrincipalDirection(Tile t1, Tile t2);

    /**
     * Returns location in the specified direction from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param direction
     *            direction to look up
     * 
     * @return location in <code>direction</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Aim direction);

    /**
     * this method returns all successors "next tiles" this for SearchTarget
     * 
     * @param target
     * @param isNextMove
     * @return
     */
    public List<SearchTarget> getSuccessorsForSearch(SearchTarget target, boolean isNextMove);

    /**
     * returns the safest next tile to the Tile position with a lookup on the influenceMap
     * 
     * @param position
     * @param influenceMap
     * @return
     */
    public Tile getSafestNeighbour(Tile position, InfluenceMap influenceMap);

    /**
     * calculates the center of all Tiles in {@code cluster}
     * 
     * @param cluster
     * @return the cluster center
     */
    public Tile getClusterCenter(List<Tile> cluster);

    /**
     * Calculates the manhattan distance to the next impassable tile in the given direction.
     * 
     * @param origin
     * @param direction
     * @return the manhattan distance
     */
    public int getManhattanDistanceToNextImpassableTile(Tile origin, Aim direction);

    /**
     * returns all tiles in one direction until it is impassable
     * 
     * @param origin
     * @param direction
     * @param maxDistnace
     * @return tiles in one direction
     */
    List<Tile> getTilesToNextImpassableTile(Tile origin, Aim direction, int maxDistnace);

}