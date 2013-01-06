package api.entities;

/**
 * This interface represents a unit on the map.
 * 
 * @author kases1, kustl1
 * 
 */
public interface Unit {

    /**
     * 
     * @return true if the unit is ours
     */
    public boolean isMine();

    /**
     * 
     * @return the integer representing the player (0 means us, any other number represents the opponents)
     */
    public int getPlayer();

    /**
     * 
     * @return the Tile where the unit currently stands
     */
    public Tile getTile();

}