package api.test;

import java.util.List;

import api.entities.Tile;
import api.map.AbstractWraparoundMap;
import api.pathfinder.SearchTarget;
import api.strategy.InfluenceMap;

/**
 * Simple map for unit tests that need to test the abstract methods on {@link AbstractWraparoundMap}.
 * 
 * @author S. Käser, L. Kuster
 * 
 */
class SimpleUnitTestMap extends AbstractWraparoundMap {

    public SimpleUnitTestMap(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public List<SearchTarget> getSuccessorsForPathfinding(SearchTarget currentPosition, boolean isNextMove) {
        return null;
    }

    @Override
    public List<SearchTarget> getSuccessorsForSearch(SearchTarget target, boolean isNextMove) {
        return null;
    }

    @Override
    public Tile getSafestNeighbour(Tile tile, InfluenceMap influenceMap) {
        return null;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public boolean isPassable(Tile tile) {
        return false;
    }

    @Override
    public boolean isVisible(Tile tile) {
        return false;
    }

}