package api.map;

import api.entities.Tile;

/**
 * the interface describes with method must be implemented for beeing a TileMap
 * 
 * @author kases1, kustl1
 * 
 */
public interface TileMap {
    /**
     * 
     * @return the number of rows in the map
     */
    public int getRows();

    /**
     * 
     * @return the number of cols in the map
     */
    public int getCols();

    /**
     * 
     * @return the map's {@link WorldType}
     */
    public WorldType getWorldType();

    /**
     * 
     * @param tile
     * @return true if we can pass through this tile (type)
     */
    public boolean isPassable(Tile tile);

    /**
     * 
     * @param tile
     * @return true if the tile is visible
     */
    public boolean isVisible(Tile tile);

    /**
     * Returns location with the specified offset from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param offset
     *            offset to look up
     * 
     * @return location with <code>offset</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Tile offset);

    /**
     * Returns the beeline distance to destination
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * @return the beeline distance
     */
    public double beelineTo(Tile t1, Tile t2);

    /**
     * Returns the Manhattan distance to the given destination
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * @return the manhattan distance
     */
    public int manhattanDistance(Tile t1, Tile t2);

    /**
     * Calculates squared distance between two locations on the game map.
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * 
     * @return the squared distance between <code>t1</code> and <code>t2</code>
     */
    public int getSquaredDistance(Tile t1, Tile t2);

}