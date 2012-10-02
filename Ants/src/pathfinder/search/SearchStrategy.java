package pathfinder.search;

import java.util.List;

import pathfinder.PathFinder;
import pathfinder.entities.SearchTarget;
import pathfinder.entities.Tile;

/***
 * definitions for a searchstrategy
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class SearchStrategy {

    public SearchStrategy(PathFinder f) {
        pathFinder = f;
    }

    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    public abstract List<Tile> search(SearchTarget from, SearchTarget to);

    public enum WorldType {
        Pizza,
        Globe
    };

    protected PathFinder pathFinder;
    protected int maxCost;
    protected Tile searchSpace1;
    protected Tile searchSpace2;

    public void setSearchSpace(Tile p1, Tile p2) {
        this.searchSpace1 = p1;
        this.searchSpace2 = p2;
    }
    
    protected boolean inSearchSpace(SearchTarget areaCheck) {
        return areaCheck.isInSearchSpace(searchSpace1, searchSpace2);
    }
    
    public void setMaxCost(int i) {
        maxCost = i;
    }

    public List<Tile> search(int xStart, int yStart, int xTarget, int yTarget) {

        Tile start = new Tile(xStart, yStart);
        Tile target = new Tile(xTarget, yTarget);
        return search(start, target);
    }
}
