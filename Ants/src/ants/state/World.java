package ants.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.RuntimeErrorException;

import ants.entities.Ant;
import ants.entities.Ilk;
import api.AbstractWraparoundMap;
import api.Aim;
import api.SearchTarget;
import api.Tile;
import api.Unit;
import api.UnitMap;

/**
 * This class holds state about the game world.
 * 
 * @author kases1,kustl1
 * 
 */
public class World extends AbstractWraparoundMap implements UnitMap {

    private int rows;

    private int cols;

    private int viewRadius2;

    private int attackRadius2;

    private int spawnRadius2;

    private Ilk map[][];

    private boolean visible[][];

    private Set<Tile> visionOffsets;

    private Set<Tile> myHills = new HashSet<Tile>();

    private Set<Tile> enemyHills = new HashSet<Tile>();

    private Set<Tile> foodTiles = new HashSet<Tile>();

    public World(int rows, int cols, int viewRadius2, int attackRadius2, int spawnRadius2) {
        this.rows = rows;
        this.cols = cols;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
        map = new Ilk[rows][cols];
        for (Ilk[] row : map) {
            Arrays.fill(row, Ilk.LAND);
        }
        visible = new boolean[rows][cols];
        for (boolean[] row : visible) {
            Arrays.fill(row, false);
        }
        // calc vision offsets
        visionOffsets = new HashSet<Tile>();
        int mx = (int) Math.sqrt(viewRadius2);
        for (int row = -mx; row <= mx; ++row) {
            for (int col = -mx; col <= mx; ++col) {
                int d = row * row + col * col;
                if (d <= viewRadius2) {
                    visionOffsets.add(new Tile(row, col));
                }
            }
        }
    }

    /**
     * Clear the game state
     * 
     * @param myAnts
     *            my Ants from last turn
     * @param enemyAnts
     *            enemy Ants from last turn
     */
    public void clearState(Collection<Ant> myAnts, Collection<Ant> enemyAnts) {
        clearIlk(myAnts);
        clearIlk(enemyAnts);
        clearDeadAnts();
        myHills.clear();
        clearVisibleEnemyHills();
        clearFood();
    }

    /**
     * This clears all visible enemy hills, but retains those that are out of view this turn.
     */
    private void clearVisibleEnemyHills() {
        for (Iterator<Tile> iter = enemyHills.iterator(); iter.hasNext();) {
            Tile hill = iter.next();
            if (isVisible(hill))
                iter.remove();
        }
    }

    private void clearIlk(Collection<Ant> ants) {
        for (Ant myAnt : ants) {
            setIlk(myAnt.getTile(), Ilk.LAND);
        }
    }

    private void clearFood() {
        for (Tile food : foodTiles) {
            setIlk(food, Ilk.LAND);
        }
        foodTiles.clear();
    }

    private void clearDeadAnts() {
        // currently we do not have list of dead ants, so iterate over all map
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (map[row][col] == Ilk.DEAD) {
                    map[row][col] = Ilk.LAND;
                }
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getViewRadius2() {
        return viewRadius2;
    }

    public int getAttackRadius2() {
        return attackRadius2;
    }

    public int getSpawnRadius2() {
        return spawnRadius2;
    }

    public Ilk getIlk(Tile tile) {
        return map[tile.getRow()][tile.getCol()];
    }

    public Ilk getIlk(Tile tile, Aim direction) {
        Tile newTile = getTile(tile, direction);
        return getIlk(newTile);
    }

    public void setIlk(Tile tile, Ilk ilk) {
        map[tile.getRow()][tile.getCol()] = ilk;
    }

    /**
     * Update information about tile visibility using the vision abilities of our ants.
     * 
     * @param myAnts
     */
    public void updateVision(Collection<Ant> myAnts) {
        for (boolean[] row : visible) {
            Arrays.fill(row, false);
        }
        for (Ant ant : myAnts) {
            for (Tile locOffset : visionOffsets) {
                Tile newLoc = getTile(ant.getTile(), locOffset);
                visible[newLoc.getRow()][newLoc.getCol()] = true;
            }
        }
    }

    /**
     * Is there food within view distance of the given tile?
     * 
     * @param tile
     * @return
     */
    public boolean isFoodNearby(Tile tile) {
        for (Tile foodTile : foodTiles) {
            if (getSquaredDistance(foodTile, tile) < viewRadius2)
                return true;
        }
        return false;
    }

    /**
     * 
     * @return a set containing all my hills locations
     */
    public Set<Tile> getMyHills() {
        return myHills;
    }

    /**
     * 
     * @return a set containing all enemy hills locations
     */
    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }

    /**
     * 
     * @return a set containing all food locations
     */
    public Set<Tile> getFoodTiles() {
        return foodTiles;
    }

    /**
     * Calculates distance between two locations on the game map.
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getSquaredDistance(Tile t1, Tile t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }

    /**
     * Updates game state information about hills locations.
     * 
     * @param owner
     *            owner of hill
     * @param tile
     *            location on the game map to be updated
     */
    public void updateHills(int owner, Tile tile) {
        if (owner > 0)
            enemyHills.add(tile);
        else
            myHills.add(tile);
    }

    /**
     * For testing only
     */
    public void setEverythingVisibleAndPassable() {
        visible = new boolean[rows][cols];
        for (boolean[] row : visible) {
            Arrays.fill(row, true);
        }

    }

    /**
     * for testing only
     * 
     * @param tile
     * @param tile2
     */
    public void setWater(Tile tile, Tile tile2) {
        if (tile.getRow() > tile2.getRow() || tile.getCol() > tile2.getCol())
            throw new RuntimeErrorException(null, "Invalid water area");

        for (int r = tile.getRow(); r < tile2.getRow(); r++)
            for (int c = tile.getCol(); c < tile2.getCol(); c++)
                setIlk(new Tile(r, c), Ilk.WATER);
    }

    /**
     * For testing only
     * 
     * @param path
     */
    // TODO still needed?
    // public void debugPathOnMap(List<Tile> path) {
    // for (int r = 0; r < getRows(); r++) {
    // String row = "";
    // for (int c = 0; c < getCols(); c++) {
    // Tile t = new Tile(r, c);
    // Ilk lk = getIlk(t);
    //
    // if (path != null && path.contains(t)) {
    // row += "P";
    // } else if (lk.isPassable()) {
    // row += " ";
    // } else {
    // row += "W";
    // }
    // }
    // LiveInfo.debug(LogCategory.JUNIT, row);
    // row = "";
    // }
    //
    // }

    public List<SearchTarget> getSuccessor(SearchTarget target, boolean isNextMove) {
        Tile state = target.getTargetTile();
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if (isPassable(getTile(state, Aim.NORTH), isNextMove))
            list.add(getTile(state, Aim.NORTH));
        if (isPassable(getTile(state, Aim.SOUTH), isNextMove))
            list.add(getTile(state, Aim.SOUTH));
        if (isPassable(getTile(state, Aim.WEST), isNextMove))
            list.add(getTile(state, Aim.WEST));
        if (isPassable(getTile(state, Aim.EAST), isNextMove))
            list.add(getTile(state, Aim.EAST));
        return list;
    }

    private boolean isPassable(Tile tile, boolean nextMove) {
        return isPassable(tile) && !isOccupiedForNextMove(nextMove);
    }

    @Override
    public boolean isPassable(Tile tile) {
        return Ants.getWorld().getIlk(tile.getTargetTile()).isPassable();
    }

    private boolean isOccupiedForNextMove(boolean bParentNode) {
        if (!bParentNode) // we are on the 2nd level of the search tree
            return Ants.getOrders().getOrders().containsValue(this);
        return false;
    }

    /**
     * Returns true if a location is visible this turn
     * 
     * @param tile
     *            location on the game map
     * 
     * @return true if the location is visible
     */
    @Override
    public boolean isVisible(Tile tile) {
        return visible[tile.getRow()][tile.getCol()];
    }

    @Override
    public Collection<Unit> getUnits(int player) {
        Set<Unit> playersAnts = new HashSet<Unit>();
        for (Unit unit : Ants.getPopulation().getEnemyAnts()) {
            if (unit.getPlayer() == player)
                playersAnts.add(unit);
        }
        return playersAnts;
    }

    @Override
    public Set<Integer> getPlayers() {
        return Ants.getPopulation().getPlayers();
    }

}
