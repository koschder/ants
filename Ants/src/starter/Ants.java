package starter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import starter.Logger.LogCategory;
import starter.mission.Mission;

/**
 * Holds all game data and current game state.
 */
public enum Ants {
    INSTANCE;
    /** Maximum map size. */
    public static final int MAX_MAP_SIZE = 256 * 2;

    private int loadTime;

    private int turnTime;

    private World world;

    private int turns;

    private long turnStartTime;

    private int turn = 0;

    private Population population;

    private Set<Mission> missions = new HashSet<Mission>();

    private Map<Tile, Move> orders = new HashMap<Tile, Move>();

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
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2,
            int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.turns = turns;
        this.world = new World(rows, cols, viewRadius2, attackRadius2, spawnRadius2);
        this.population = new Population();
    }

    public static Ants getAnts() {
        return INSTANCE;
    }

    public static World getWorld() {
        return INSTANCE.world;
    }

    public static Population getPopulation() {
        return INSTANCE.population;
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
     * Returns maximum number of turns the game will be played.
     * 
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
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

    public boolean putOrder(Ant ant, Aim direction) {
        // Track all moves, prevent collisions
        Tile newLoc = getWorld().getTile(ant.getTile(), direction);
        if (getWorld().getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            orders.put(newLoc, new Move(ant.getTile(), direction));
            ant.setNextTile(newLoc);
            getPopulation().addEmployedAnt(ant);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clears game state information about my ants locations.
     */
    public void clearMyAnts() {
        for (Ant myAnt : getPopulation().getMyAnts()) {
            getWorld().setIlk(myAnt.getTile(), Ilk.LAND);
        }
        getPopulation().clearMyAnts();
    }

    /**
     * Clears game state information about enemy ants locations.
     */
    public void clearEnemyAnts() {
        for (Ant enemyAnt : getPopulation().getEnemyAnts()) {
            getWorld().setIlk(enemyAnt.getTile(), Ilk.LAND);
        }
        getPopulation().getEnemyAnts().clear();
    }

    /**
     * Clears game state information about food locations.
     */
    public void clearFood() {
        for (Tile food : getWorld().getFoodTiles()) {
            getWorld().setIlk(food, Ilk.LAND);
        }
        getWorld().getFoodTiles().clear();
    }

    /**
     * Clears game state information about my hills locations.
     */
    public void clearMyHills() {
        getWorld().getMyHills().clear();
    }

    /**
     * Clears game state information about enemy hills locations.
     */
    public void clearEnemyHills() {
        getWorld().getEnemyHills().clear();
    }

    /**
     * Clears game state information about dead ants locations.
     */
    public void clearDeadAnts() {
        getWorld().clearDeadAnts();
    }

    /**
     * Clears visible information
     */
    public void clearVision() {
        // TODO included in world.updateVision, do we still need it?
    }

    /**
     * Calculates visible information
     */
    public void setVision() {
        getWorld().updateVision(getPopulation().getMyAnts());
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
        getWorld().setIlk(tile, ilk);
        switch (ilk) {
        case FOOD:
            getWorld().getFoodTiles().add(tile);
            break;
        case MY_ANT:
            getPopulation().getMyAnts().add(new Ant(tile, Ant.MINE));
            break;
        case ENEMY_ANT:
            getPopulation().getEnemyAnts().add(new Ant(tile, owner));
            break;
        }
    }

    public void update(Ilk ilk, Tile tile) {
        update(ilk, tile, Integer.MIN_VALUE);
    }

    public void calculateDistances() {
        for (Ant enemy : getPopulation().getEnemyAnts()) {
            for (Ant myAnt : getPopulation().getMyAnts()) {
                addEnemyPair(enemy, myAnt);
            }
            for (Ant other : getPopulation().getEnemyAnts()) {
                if (other.equals(enemy))
                    continue;
                if (other.getPlayer() == enemy.getPlayer())
                    addFriendPair(enemy, other);
                else
                    addEnemyPair(enemy, other);
            }
        }
        for (Ant ant : getPopulation().getMyAnts()) {
            for (Ant friend : getPopulation().getMyAnts()) {
                if (friend.equals(ant))
                    continue;
                addFriendPair(ant, friend);
            }
        }
    }

    public void initOrders() {
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : getWorld().getMyHills()) {
            orders.put(myHill, null);
        }
    }

    public void issueOrders() {
        for (Move move : orders.values()) {
            if (move != null) {
                final String order = "o " + move.getTile().getRow() + " " + move.getTile().getCol() + " "
                        + move.getDirection().getSymbol();
                Logger.debug(LogCategory.ORDERS, "Issuing order: %s", order);
                System.out.println(order);
            }
        }
    }

    private void addEnemyPair(Ant anta, Ant antb) {
        final int distance = getWorld().getSquaredDistance(antb.getTile(), anta.getTile());
        anta.addEnemy(antb, distance);
        antb.addEnemy(anta, distance);
    }

    private void addFriendPair(Ant anta, Ant antb) {
        final int distance = getWorld().getSquaredDistance(antb.getTile(), anta.getTile());
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
        Logger.debug(LogCategory.EXECUTE_MISSIONS, "New mission created: %s", newMission);
        missions.add(newMission);
    }

    public int getTurn() {
        return turn;
    }

    public void updateTurn() {
        turn++;
    }
}
