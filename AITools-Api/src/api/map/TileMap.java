package api.map;

import api.entities.Tile;

public interface TileMap {

    public int getRows();

    public int getCols();

    public WorldType getWorldType();

    /***
     * 
     * @param tile
     * @return true if we can pass through this tile (type)
     */
    public boolean isPassable(Tile tile);

    /***
     * do we know the character of this field.
     * 
     * @param tile
     * @return
     */
    public boolean isVisible(Tile tile);

    public abstract Tile getTile(Tile tile, Tile offset);

    /***
     * Returns the beeline distance to destination {@code dest}
     * 
     * @param dest
     * @return
     */
    public double beelineTo(Tile tStart, Tile tEnd);

    /***
     * Returns the Manhattan distance to the overgiven destination {@code dest}
     * 
     * @param dest
     * @return
     */
    public int manhattanDistance(Tile tStart, Tile tEnd);

}