package starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import starter.Logger.LogCategory;
import starter.mission.Mission;

/**
 * Holds all game data and current game state.
 */
public class Ants {
    /** Maximum map size. */
    public static final int MAX_MAP_SIZE = 256 * 2;

    private final int loadTime;

    private final int turnTime;

    private final int rows;

    private final int cols;

    private final int turns;

    private final int viewRadius2;

    private final int attackRadius2;

    private final int spawnRadius2;

    private final boolean visible[][];

    private final Set<Tile> visionOffsets;

    private long turnStartTime;

    private final Ilk map[][];

    private final Set<Ant> myAnts = new HashSet<Ant>();
    private Set<Ant> myUnemployedAnts = null;
    private Set<Ant> employedAnts = new HashSet<Ant>();

    private final Set<Ant> enemyAnts = new HashSet<Ant>();

    private final Set<Tile> myHills = new HashSet<Tile>();

    private final Set<Tile> enemyHills = new HashSet<Tile>();

    private final Set<Tile> foodTiles = new HashSet<Tile>();

    private final Set<Mission> missions = new HashSet<Mission>();

    private final Map<Tile, Move> orders = new HashMap<Tile, Move>();

    /**
     * Creates new {@link Ants} object.
     * 
     * @param loadTime
     *            timeout for initializing and setting up the bot on turn 0
     * @param turnTime
     *            timeout for a single game turn, starting with turn 1
     * @param rows
     *            game map height
     * @param cols
     *            game map width
     * @param turns
     *            maximum number of turns the game will be played
     * @param viewRadius2
     *            squared view radius of each ant
     * @param attackRadius2
     *            squared attack radius of each ant
     * @param spawnRadius2
     *            squared spawn radius of each ant
     */
    public Ants(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2,
            int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
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
     * Returns timeout for initializing and setting up the bot on turn 0.
     * 
     * @return timeout for initializing and setting up the bot on turn 0
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * Returns timeout for a single game turn, starting with turn 1.
     * 
     * @return timeout for a single game turn, starting with turn 1
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * Returns game map height.
     * 
     * @return game map height
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns game map width.
     * 
     * @return game map width
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns maximum number of turns the game will be played.
     * 
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Returns squared view radius of each ant.
     * 
     * @return squared view radius of each ant
     */
    public int getViewRadius2() {
        return viewRadius2;
    }

    /**
     * Returns squared attack radius of each ant.
     * 
     * @return squared attack radius of each ant
     */
    public int getAttackRadius2() {
        return attackRadius2;
    }

    /**
     * Returns squared spawn radius of each ant.
     * 
     * @return squared spawn radius of each ant
     */
    public int getSpawnRadius2() {
        return spawnRadius2;
    }

    /**
     * Sets turn start time.
     * 
     * @param turnStartTime
     *            turn start time
     */
    public void setTurnStartTime(long turnStartTime) {
        this.turnStartTime = turnStartTime;
    }

    public long getTurnStartTime() {
        return turnStartTime;
    }

    /**
     * Returns how much time the bot has still has to take its turn before timing out.
     * 
     * @return how much time the bot has still has to take its turn before timing out
     */
    public int getTimeRemaining() {
        return turnTime - (int) (System.currentTimeMillis() - turnStartTime);
    }

    /**
     * Returns ilk at the specified location.
     * 
     * @param tile
     *            location on the game map
     * 
     * @return ilk at the <cod>tile</code>
     */
    public Ilk getIlk(Tile tile) {
        return map[tile.getRow()][tile.getCol()];
    }

    /**
     * Sets ilk at the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param ilk
     *            ilk to be set at <code>tile</code>
     */
    public void setIlk(Tile tile, Ilk ilk) {
        map[tile.getRow()][tile.getCol()] = ilk;
    }

    /**
     * Returns ilk at the location in the specified direction from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param direction
     *            direction to look up
     * 
     * @return ilk at the location in <code>direction</code> from <cod>tile</code>
     */
    public Ilk getIlk(Tile tile, Aim direction) {
        Tile newTile = getTile(tile, direction);
        return map[newTile.getRow()][newTile.getCol()];
    }

    /**
     * Returns location in the specified direction from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param direction
     *            direction to look up
     * 
     * @return location in <code>direction</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Aim direction) {
        int row = (tile.getRow() + direction.getRowDelta()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (tile.getCol() + direction.getColDelta()) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Tile(row, col);
    }

    /**
     * Returns location with the specified offset from the specified location.
     * 
     * @param tile
     *            location on the game map
     * @param offset
     *            offset to look up
     * 
     * @return location with <code>offset</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Tile offset) {
        int row = (tile.getRow() + offset.getRow()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (tile.getCol() + offset.getCol()) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Tile(row, col);
    }

    /**
     * Returns a set containing all my ants locations.
     * 
     * @return a set containing all my ants locations
     */
    public Set<Ant> getMyAnts() {
        return myAnts;
    }

    public Collection<Ant> getMyUnemployedAnts() {
        if (myUnemployedAnts == null)
            myUnemployedAnts = new HashSet<Ant>(myAnts);
        for (Iterator<Ant> it = employedAnts.iterator(); it.hasNext();) {
            Ant ant = it.next();
            if (!myUnemployedAnts.remove(ant)) {
                Logger.log(LogCategory.ERROR, "Could not remove ant %s Tile: %s of unemployedAnts size: %s", ant,
                        ant.getTile(), myUnemployedAnts.size());
            } else {
                Logger.log(LogCategory.TRACE, "Ant %s Tile: %s marked as employed", ant, ant.getTile());
            }
            it.remove();
        }
        return myUnemployedAnts;
    }

    public boolean putOrder(Ant ant, Aim direction) {
        // Track all moves, prevent collisions
        Tile newLoc = getTile(ant.getTile(), direction);
        if (getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            employedAnts.add(ant);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a set containing all enemy ants locations.
     * 
     * @return a set containing all enemy ants locations
     */
    public Set<Ant> getEnemyAnts() {
        return enemyAnts;
    }

    /**
     * Returns a set containing all my hills locations.
     * 
     * @return a set containing all my hills locations
     */
    public Set<Tile> getMyHills() {
        return myHills;
    }

    /**
     * Returns a set containing all enemy hills locations.
     * 
     * @return a set containing all enemy hills locations
     */
    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }

    /**
     * Returns a set containing all food locations.
     * 
     * @return a set containing all food locations
     */
    public Set<Tile> getFoodTiles() {
        return foodTiles;
    }

    /**
     * Returns true if a location is visible this turn
     * 
     * @param tile
     *            location on the game map
     * 
     * @return true if the location is visible
     */
    public boolean isVisible(Tile tile) {
        return visible[tile.getRow()][tile.getCol()];
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
     * Returns one or two orthogonal directions from one location to the another.
     * 
     * @param t1
     *            one location on the game map
     * @param t2
     *            another location on the game map
     * 
     * @return orthogonal directions from <code>t1</code> to <code>t2</code>
     */
    public List<Aim> getDirections(Tile t1, Tile t2) {
        List<Aim> directions = new ArrayList<Aim>();
        if (t1.getRow() < t2.getRow()) {
            if (t2.getRow() - t1.getRow() >= rows / 2) {
                directions.add(Aim.NORTH);
            } else {
                directions.add(Aim.SOUTH);
            }
        } else if (t1.getRow() > t2.getRow()) {
            if (t1.getRow() - t2.getRow() >= rows / 2) {
                directions.add(Aim.SOUTH);
            } else {
                directions.add(Aim.NORTH);
            }
        }
        if (t1.getCol() < t2.getCol()) {
            if (t2.getCol() - t1.getCol() >= cols / 2) {
                directions.add(Aim.WEST);
            } else {
                directions.add(Aim.EAST);
            }
        } else if (t1.getCol() > t2.getCol()) {
            if (t1.getCol() - t2.getCol() >= cols / 2) {
                directions.add(Aim.EAST);
            } else {
                directions.add(Aim.WEST);
            }
        }
        return directions;
    }

    /**
     * Clears game state information about my ants locations.
     */
    public void clearMyAnts() {
        for (Ant myAnt : myAnts) {
            map[myAnt.getTile().getRow()][myAnt.getTile().getCol()] = Ilk.LAND;
        }
        myAnts.clear();
        myUnemployedAnts = null;
    }

    /**
     * Clears game state information about enemy ants locations.
     */
    public void clearEnemyAnts() {
        for (Ant enemyAnt : enemyAnts) {
            map[enemyAnt.getTile().getRow()][enemyAnt.getTile().getCol()] = Ilk.LAND;
        }
        enemyAnts.clear();
    }

    /**
     * Clears game state information about food locations.
     */
    public void clearFood() {
        for (Tile food : foodTiles) {
            map[food.getRow()][food.getCol()] = Ilk.LAND;
        }
        foodTiles.clear();
    }

    /**
     * Clears game state information about my hills locations.
     */
    public void clearMyHills() {
        myHills.clear();
    }

    /**
     * Clears game state information about enemy hills locations.
     */
    public void clearEnemyHills() {
        enemyHills.clear();
    }

    /**
     * Clears game state information about dead ants locations.
     */
    public void clearDeadAnts() {
        // currently we do not have list of dead ants, so iterate over all map
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (map[row][col] == Ilk.DEAD) {
                    map[row][col] = Ilk.LAND;
                }
            }
        }
    }

    /**
     * Clears visible information
     */
    public void clearVision() {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                visible[row][col] = false;
            }
        }
    }

    /**
     * Calculates visible information
     */
    public void setVision() {
        for (Ant ant : myAnts) {
            for (Tile locOffset : visionOffsets) {
                Tile newLoc = getTile(ant.getTile(), locOffset);
                visible[newLoc.getRow()][newLoc.getCol()] = true;
            }
        }
    }

    /**
     * Updates game state information about new ants and food locations.
     * 
     * @param ilk
     *            ilk to be updated
     * @param tile
     *            location on the game map to be updated
     */
    public void update(Ilk ilk, Tile tile, int owner) {
        map[tile.getRow()][tile.getCol()] = ilk;
        switch (ilk) {
        case FOOD:
            foodTiles.add(tile);
            break;
        case MY_ANT:
            myAnts.add(new Ant(tile, Ant.MINE));
            break;
        case ENEMY_ANT:
            enemyAnts.add(new Ant(tile, owner));
            break;
        }
    }

    public void update(Ilk ilk, Tile tile) {
        update(ilk, tile, Integer.MIN_VALUE);
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

    public void calculateDistances() {
        for (Ant enemy : getEnemyAnts()) {
            for (Ant myAnt : getMyAnts()) {
                addEnemyPair(enemy, myAnt);
            }
            for (Ant other : getEnemyAnts()) {
                if (other.equals(enemy))
                    continue;
                if (other.getPlayer() == enemy.getPlayer())
                    addFriendPair(enemy, other);
                else
                    addEnemyPair(enemy, other);
            }
        }
        for (Ant ant : getMyAnts()) {
            for (Ant friend : getMyAnts()) {
                if (friend.equals(ant))
                    continue;
                addFriendPair(ant, friend);
            }
        }
    }

    public void initOrders() {
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : getMyHills()) {
            orders.put(myHill, null);
        }
    }

    public void issueOrders() {
        for (Move move : orders.values()) {
            if (move != null) {
                final String order = "o " + move.getTile().getRow() + " " + move.getTile().getCol() + " "
                        + move.getDirection().getSymbol();
                Logger.log(LogCategory.ORDERS, "Issuing order: %s", order);
                System.out.println(order);
            }
        }
    }

    private void addEnemyPair(Ant anta, Ant antb) {
        final int distance = getSquaredDistance(antb.getTile(), anta.getTile());
        anta.addEnemy(antb, distance);
        antb.addEnemy(anta, distance);
    }

    private void addFriendPair(Ant anta, Ant antb) {
        final int distance = getSquaredDistance(antb.getTile(), anta.getTile());
        anta.addFriend(antb, distance);
        antb.addFriend(anta, distance);
    }

    public Map<Tile, Move> getOrders() {
        return orders;
    }

    public Set<Mission> getMissions() {
        return missions;
    }

    public void addMission(Mission newMission) {
        Logger.log(LogCategory.EXECUTE_MISSIONS, "New mission created: %s", newMission);
        missions.add(newMission);
    }
}
