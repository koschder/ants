package api.strategy;

import api.entities.Tile;
import api.search.PathPiece;

/**
 * This interface defines the methods an InfluenceMap must provide
 * 
 * @author kases1, kustl1
 * 
 */
public interface InfluenceMap {

    /**
     * for a tile on the map, we calculate the safety by subtracting the enemies influences of our influence
     * 
     * @param tile
     * @return negative if enemy is stronger, positive if we are stronger.
     */
    public int getSafety(Tile tile);

    /**
     * returns the player's influence on a specific tile.
     * 
     * @param player
     * @param tile
     * @return influence of player
     */
    public int getInfluence(Integer player, Tile tile);

    /**
     * get the total influence over the whole map
     * 
     * @param player
     *            to calculate the total influence
     * @return total influence
     */
    public int getTotalInfluence(Integer player);

    /**
     * @return the total influence of all enemies
     */
    public int getTotalOpponentInfluence();

    /**
     * updates the influence map (each turn or after a longer period)
     * 
     * @param map
     *            containing all combat units which creates an influence
     */
    public void update(SearchableUnitMap map);

    /**
     * if the pathfinder considers the influence map while search a path, this method is used to get determine the cost
     * of each tile.
     * 
     * @param pathPiece
     *            a path piece
     * @return costs
     */
    public int getPathCosts(PathPiece pathPiece);

}
