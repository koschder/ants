package ants.state;

import ants.entities.Ant;
import ants.entities.Ilk;
import ants.entities.Tile;
import ants.search.Clustering;

/**
 * Holds all game data and current game state.
 */
public enum Ants {
    INSTANCE;
    /** Maximum map size. */
    public static final int MAX_MAP_SIZE = 256 * 2;

    private int loadTime;

    private int turnTime;

    private int turns;

    private long turnStartTime;

    private int turn = 0;

    private World world;

    private Population population;

    private Orders orders;

    /*
     * clusters for hierarcical grah pathfidning.
     */
    private Clustering clustering;

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
        this.orders = new Orders();
        this.clustering = new Clustering(7);
    }

    public void clearState() {
        getWorld().clearState(getPopulation().getMyAnts(), getPopulation().getEnemyAnts());
        getPopulation().clearState();
        getOrders().clearState();
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
        case ENEMY_ANT:
            getPopulation().addAnt(tile, owner);
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

    /**
     * Increment turn counter and set turn start time.
     */
    public void nextTurn() {
        turn++;
        turnStartTime = System.currentTimeMillis();
    }

    /*
     * Accessors
     */

    public int getTurn() {
        return turn;
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

    public static Orders getOrders() {
        return INSTANCE.orders;
    }

    public static Clustering getClusters() {
        return INSTANCE.clustering;
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
     * Returns the start time of the current turn.
     * 
     * @return start time of the current turn.
     */
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
}
