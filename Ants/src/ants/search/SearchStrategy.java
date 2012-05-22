package ants.search;

import java.util.List;

import ants.entities.SearchTarget;
import ants.entities.Tile;

public interface SearchStrategy {
    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public List<Tile> bestPath(SearchTarget from, SearchTarget to);

    public void setMaxCost(int i);

    void setSearchSpace(Tile p1, Tile p2);
}
