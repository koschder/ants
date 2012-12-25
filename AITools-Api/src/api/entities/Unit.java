package api.entities;

/**
 * interface Unit, getTile() for the location unit, a getPlayer() to determine the owner
 * 
 * @author kaeserst, kustl1
 * 
 */
public interface Unit {

    public boolean isMine();

    public int getPlayer();

    public Tile getTile();

}