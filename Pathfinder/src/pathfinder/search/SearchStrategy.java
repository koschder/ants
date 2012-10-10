package pathfinder.search;

import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.PATHFINDING);

    public SearchStrategy(PathFinder f) {
        pathFinder = f;
    }

    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    protected abstract List<Tile> searchPath(SearchTarget from, SearchTarget to);

    /***
     * searchs the path and logs the calls
     * 
     * @param from
     * @param to
     * @return the path if found, or null if path is empty
     */
    public List<Tile> search(SearchTarget from, SearchTarget to) {
        long start = System.currentTimeMillis();

        List<Tile> path = searchPath(from, to);

        setTimeElapsed(System.currentTimeMillis() - start);
        log(path, from, to);

        return path;
    }

    /***
     * logs for every search the main facts about the done search
     * 
     * @param path
     * @param from
     * @param to
     */
    private void log(List<Tile> path, SearchTarget from, SearchTarget to) {
        String pathlength = path != null ? "path has size of " + path.size() : "path not found";
        String searchSpaceInfo = "";
        if (searchSpace1 != null) {
            searchSpaceInfo = String.format("In searchspace: %s to %s", searchSpace1, searchSpace2);
        }
        LOGGER.debug("PathFinder: %s ended. Path from: %s to %s %s took %s miliseconds. %s ", this.getClass(),
                from.toShortString(), to.toShortString(), searchSpaceInfo, getTimeElapsed(), pathlength);

    }

    protected PathFinder pathFinder;
    protected int maxCost;
    protected Tile searchSpace1;
    protected Tile searchSpace2;
    private long timeElapsed = -1;

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
        return searchPath(start, target);
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
