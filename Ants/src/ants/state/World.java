package ants.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javax.management.RuntimeErrorException;

import logging.Logger;
import logging.LoggerFactory;
import pathfinder.PathFinder.Strategy;
import pathfinder.entities.Node;
import ants.LogCategory;
import ants.entities.Ant;
import ants.entities.Ilk;
import api.entities.Aim;
import api.entities.Tile;
import api.entities.Unit;
import api.map.AbstractWraparoundMap;
import api.pathfinder.SearchTarget;
import api.pathfinder.SearchableUnitMap;
import api.strategy.InfluenceMap;

/**
 * This class holds state about the game world.
 * 
 * @author kases1,kustl1
 * 
 */
public class World extends AbstractWraparoundMap implements SearchableUnitMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.WORLD);

    private int viewRadius2;

    private int attackRadius2;

    private int spawnRadius2;

    private Ilk map[][];

    private boolean visible[][];

    private Set<Tile> visionOffsets;

    private Set<Tile> myHills = new HashSet<Tile>();

    private Set<Tile> newMyHills = new HashSet<Tile>();

    private Set<Tile> enemyHills = new HashSet<Tile>();

    private Set<Tile> newEnemyHills = new HashSet<Tile>();

    private Set<Tile> foodTiles = new HashSet<Tile>();

    /**
     * default constructor for testing only
     */
    public World() {
        super(-1, -1);
    }

    public World(int rows, int cols, int viewRadius2, int attackRadius2, int spawnRadius2) {
        super(rows, cols);
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
        newMyHills.clear();
        newEnemyHills.clear();
        clearFood();
    }

    /**
     * This clears all visible enemy hills, but retains those that are out of view this turn.
     */
    public void updateHills() {
        updateHills(myHills, newMyHills);
        updateHills(enemyHills, newEnemyHills);
    }

    private void updateHills(Collection<Tile> hills, Collection<Tile> newHills) {
        for (Iterator<Tile> iter = hills.iterator(); iter.hasNext();) {
            Tile hill = iter.next();
            if (newHills.remove(hill))
                continue;
            else if (isVisible(hill))
                iter.remove();
        }
        hills.addAll(newHills);
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
    public List<Tile> isFoodNearby(Tile tile) {
        List<Tile> food = new ArrayList<Tile>();
        for (Tile foodTile : foodTiles) {
            if (getSquaredDistance(foodTile, tile) < viewRadius2)
                food.add(foodTile);
        }
        return food;
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
     * Updates game state information about hills locations.
     * 
     * @param owner
     *            owner of hill
     * @param tile
     *            location on the game map to be updated
     */
    public void addHill(int owner, Tile tile) {
        if (owner > 0)
            newEnemyHills.add(tile);
        else
            newMyHills.add(tile);
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

    @Override
    public List<SearchTarget> getSuccessorsForPathfinding(SearchTarget target, boolean isNextMove) {
        return getSuccessorsInternal(target, isNextMove, false);
    }

    @Override
    public List<SearchTarget> getSuccessorsForSearch(SearchTarget target, boolean isNextMove) {
        return getSuccessorsInternal(target, isNextMove, true);
    }

    private List<SearchTarget> getSuccessorsInternal(SearchTarget target, boolean isNextMove, boolean includeOwnHills) {
        Tile state = target.getTargetTile();
        List<SearchTarget> list = new ArrayList<SearchTarget>();
        if (isPassable(getTile(state, Aim.NORTH), isNextMove, includeOwnHills))
            list.add(getTile(state, Aim.NORTH));
        if (isPassable(getTile(state, Aim.SOUTH), isNextMove, includeOwnHills))
            list.add(getTile(state, Aim.SOUTH));
        if (isPassable(getTile(state, Aim.WEST), isNextMove, includeOwnHills))
            list.add(getTile(state, Aim.WEST));
        if (isPassable(getTile(state, Aim.EAST), isNextMove, includeOwnHills))
            list.add(getTile(state, Aim.EAST));
        return list;
    }

    private boolean isPassable(Tile tile, boolean nextMove, boolean includeOwnHills) {
        return (includeOwnHills || !getMyHills().contains(tile)) && isPassable(tile)
                && !isOccupiedForNextMove(tile, nextMove);
    }

    @Override
    public boolean isPassable(Tile tile) {
        return Ants.getWorld().getIlk(tile.getTargetTile()).isPassable();
    }

    private boolean isOccupiedForNextMove(Tile t, boolean nextMove) {
        if (nextMove) // we are on the 2nd level of the search tree
            return Ants.getOrders().getOrders().containsValue(t);
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

    public List<Tile> getVisibleTiles(Ant ant) {
        return getVisibleTiles(ant.getTile());
    }

    public List<Tile> getVisibleTiles(Tile center) {
        List<Tile> visibleTiles = new ArrayList<Tile>();
        for (Tile offset : visionOffsets) {
            visibleTiles.add(getTile(center, offset));
        }
        return visibleTiles;
    }

    public int getVisibleTilesPercent() {
        int totalTiles = rows * cols;
        int visibleTiles = 0;
        for (boolean[] row : visible) {
            for (boolean col : row) {
                if (col)
                    visibleTiles++;
            }
        }
        return Math.round((visibleTiles / (float) totalTiles) * 100);
    }

    @Override
    public List<Unit> getUnits(int player) {
        List<Unit> playersAnts = new ArrayList<Unit>();
        if (player == 0) {
            playersAnts.addAll(Ants.getPopulation().getMyAnts());
        } else {
            for (Unit unit : Ants.getPopulation().getEnemyAnts()) {
                if (unit.getPlayer() == player)
                    playersAnts.add(unit);
            }
        }
        return playersAnts;
    }

    @Override
    public Set<Integer> getPlayers() {
        return Ants.getPopulation().getPlayers();
    }

    public void printIlk() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public Set<Tile> getAreaFlooded(Tile hill, int nearBy) {
        Set<Tile> floodedTiles = new HashSet<Tile>();

        PriorityQueue<Node> frontier = new PriorityQueue<Node>();
        frontier.add(new Node(hill, null, 0, 0));
        while (!frontier.isEmpty()) {

            Node node = frontier.poll();
            floodedTiles.add(node.getState().getTargetTile());

            List<SearchTarget> tiles = getSuccessorsForPathfinding(node.getState().getTargetTile(), false);
            int cost = node.getActualCost() + 1;
            for (SearchTarget child : tiles) {

                Node n = new Node(child.getTargetTile(), null, cost, cost);
                if (frontier.contains(n) || floodedTiles.contains(child.getTargetTile())) {
                    continue;
                }
                if (cost == nearBy) {
                    floodedTiles.add(child.getTargetTile());
                } else if (cost < nearBy) {
                    frontier.add(n);
                }
            }
        }

        return floodedTiles;
    }

    public boolean isInAttackZone(Tile myTile, Tile enemyTile) {
        return getSquaredDistance(myTile, enemyTile) <= getAttackRadius2();
    }

    public boolean isEasilyReachable(Tile from, Tile to) {
        return Ants.getPathFinder().search(Strategy.Simple, from, to) != null;
    }

    @Override
    public Tile getSafestNeighbour(Tile tile, InfluenceMap influenceMap) {
        String log = "";
        Tile safestTile = null;
        int bestSafety = Integer.MIN_VALUE;
        for (SearchTarget neighbour : getSuccessorsForPathfinding(tile, true)) {
            if (Ants.getOrders().isMovePossible(neighbour.getTargetTile())) {
                final int safety = influenceMap.getSafety(neighbour.getTargetTile());
                log += neighbour + " saftey: " + safety + ",";
                if (safety > bestSafety) {
                    safestTile = neighbour.getTargetTile();
                    bestSafety = safety;
                }
            }
        }
        LOGGER.debug(log);
        return safestTile;
    }
}
