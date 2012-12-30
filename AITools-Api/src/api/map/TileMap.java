package api.map;

import api.entities.Tile;

/**
 * the interface describes with method must be implemented for beeing a TileMap
 * 
 * @author kases1, kustl1
 * 
 */
public interface TileMap {

    public int getRows();

    public int getCols();

    public WorldType getWorldType();

    /**
     * 
     * @param tile
     * @return true if we can pass through this tile (type)
     */
    public boolean isPassable(Tile tile);

    /**
     * do we know the character of this field.
     * 
     * @param tile
     * @return
     */
    public boolean isVisible(Tile tile);

    public abstract Tile getTile(Tile tile, Tile offset);

    /**
     * Returns the beeline distance to destination {@code dest}
     * 
     * @param dest
     * @return
     */
    public double beelineTo(Tile t1, Tile t2);

    /**
     * Returns the Manhattan distance to the overgiven destination {@code dest}
     * 
     * @param dest
     * @return
     */
    public int manhattanDistance(Tile t1, Tile t2);

    /**
     * the squared distance between the tiles
     * 
     * @param t1
     * @param t2
     * @return
     */
    public int getSquaredDistance(Tile t1, Tile t2);

}