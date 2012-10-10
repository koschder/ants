package pathfinder.entities;

import java.util.List;

public interface SearchableMap {

    public abstract int getRows();

    public abstract int getCols();

    /***
     * 
     * @param tile
     * @return true if we can pass through this tile (type)
     */
    public abstract boolean isPassable(SearchTarget tile);

    /***
     * do we know the character of this field.
     * @param tile
     * @return
     */
    public abstract boolean isVisible(SearchTarget tile);

    /***
     * returns all neighbor fields, passable for any movable object 
     * @param currentPosition actual position
     * @param isNextMove (is this path part used for next game step or later)
     * @return all "next" positions
     */
    public abstract List<SearchTarget> getSuccessor(SearchTarget currentPosition, boolean isNextMove);

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
    public abstract List<Aim> getDirections(Tile t1, Tile t2);

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
    public abstract Tile getTile(Tile tile, Aim direction);

}