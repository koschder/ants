package pathfinder.search;

import java.util.List;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.LogCategory;
import pathfinder.PathFinder;
import pathfinder.entities.Node;
import api.entities.Tile;
import api.map.TileMap;
import api.map.WorldType;
import api.pathfinder.SearchTarget;

/***
 * definitions for a searchstrategy
 * 
 * @author kases1, kustl1
 * 
 */
public abstract class SearchStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.PATHFINDING);

    protected PathFinder pathFinder;
    protected int maxCost;
    protected Tile searchSpace1;
    protected Tile searchSpace2;
    private long timeElapsed = -1;

    protected boolean useInflunceMap = false;

    public SearchStrategy(PathFinder f) {
        pathFinder = f;
        if (f.getInfluenceMap() != null) {
            useInflunceMap = true;
        }

    }

    /**
     * 
     * @param from
     * @param to
     * @return the path in a list or null if no path is found.
     */
    protected abstract List<Tile> searchPath(SearchTarget from, SearchTarget to);

    /***
     * Searches the path and logs the calls
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

    public void setSearchSpace(Tile p1, Tile p2) {
        this.searchSpace1 = p1;
        this.searchSpace2 = p2;
    }

    protected double getEstimatedCosts(Tile from, Tile to) {
        return pathFinder.getMap().beelineTo(from, to);

    }

    protected final int getActualCost(Node current, SearchTarget dest) {
        if (useInflunceMap) {
            return current.getActualCost() + pathFinder.getInfluenceMap().getPathCosts(dest);
        }
        return current.getActualCost() + dest.getCost();
    }

    protected boolean inSearchSpace(SearchTarget areaCheck) {

        if (searchSpace1 == null || searchSpace2 == null)
            return true; // no searchspace defined.

        if (!(areaCheck instanceof Tile))
            return true;

        Tile t = (Tile) areaCheck;

        if (checkSpace(t, searchSpace1, searchSpace2))
            return true;

        TileMap m = pathFinder.getMap();

        if (m.getWorldType() == WorldType.Pizza)
            return false;

        // maybe the searchspace is wraparound
        if (checkSpace(new Tile(t.getRow(), t.getCol() + m.getCols()), searchSpace1, searchSpace2))
            return true;

        if (checkSpace(new Tile(t.getRow() + m.getRows(), t.getCol()), searchSpace1, searchSpace2))
            return true;

        if (checkSpace(new Tile(m.getRows(), t.getCol() + m.getCols()), searchSpace1, searchSpace2))
            return true;

        return false;

    }

    private boolean checkSpace(Tile t, Tile sp1, Tile sp2) {
        if (sp1.getRow() <= t.getRow() && sp1.getCol() <= t.getCol())
            if (sp2.getRow() >= t.getRow() && sp2.getCol() >= t.getCol())
                return true;
        return false;
    }

    public void setMaxCost(int i) {
        maxCost = i;
    }

    private long getTimeElapsed() {
        return timeElapsed;
    }

    private void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
