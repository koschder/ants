package ants.search;

import java.util.List;

import ants.entities.SearchTarget;
import ants.entities.Tile;

/***
 * definitions for a searchstrategy
 * @author kases1, kustl1
 *
 */
public interface SearchStrategy {
    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public List<Tile> bestPath(SearchTarget from, SearchTarget to);

    /***
     * set the maximun cost of a path.
     * @param maxCost
     */
    public void setMaxCost(int maxCost);

    /**
     * setting an area were we want to serach in.
     * @param p1
     * @param p2
     */
    void setSearchSpace(Tile p1, Tile p2);
}
