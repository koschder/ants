package api.pathfinder;

import java.util.List;

import api.entities.Aim;
import api.entities.Tile;
import api.map.TileMap;
import api.strategy.InfluenceMap;

public interface SearchableMap extends TileMap {

    /***
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

    public List<SearchTarget> getSuccessorsForSearch(SearchTarget target, boolean isNextMove);

    public Tile getSafestNeighbour(Tile tile, InfluenceMap influenceMap);

}