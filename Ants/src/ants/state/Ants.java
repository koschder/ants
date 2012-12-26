package ants.state;

import pathfinder.PathFinder;
import pathfinder.SimplePathFinder;
import pathfinder.entities.Clustering;
import ants.entities.Ilk;
import ants.profile.Profile;
import api.entities.Tile;
import api.strategy.InfluenceMap;

/**
 * Container class for the game state. Provides access to game metadata (such as turnTime, startTime, ...) as well as
 * access to information about the world (via {@link World}), the ants (via {@link Population}), the orders (via
 * {@link Orders}), and the {@link Clustering} of the map.
 * 
 * @author kases1, kustl1
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

    private PathFinder pathFinder;

    private InfluenceMap influence;

    private Profile profile;

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
            int spawnRadius2, Profile profile) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.turns = turns;
        this.turnStartTime = System.currentTimeMillis();
        this.world = new World(rows, cols, viewRadius2, attackRadius2, spawnRadius2);
        this.population = new Population();
        this.orders = new Orders();
        this.pathFinder = new SimplePathFinder(world, influence);
        this.profile = profile;
    }

    /**
     * Clears all turn-scoped information.
     */
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
     * Updates game state information about new ants locations.
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

    /**
     * Updates game state information.
     * 
     * @param ilk
     *            ilk to be updated
     * @param tile
     *            location on the game map to be updated
     */
    public void update(Ilk ilk, Tile tile) {
        update(ilk, tile, Integer.MIN_VALUE);
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

    /**
     * 
     * @return the singleton instance of Ants
     */
    public static Ants getAnts() {
        return INSTANCE;
    }

    /**
     * 
     * @return the singleton instance of World
     */
    public static World getWorld() {
        return INSTANCE.world;
    }

    /**
     * For testing only: set a Population instance
     */
    public static void setWorld(World world) {
        INSTANCE.world = world;
    }

    /**
     * 
     * @return the singleton instance of Population
     */
    public static Population getPopulation() {
        return INSTANCE.population;
    }

    /**
     * For testing only: set a Population instance
     */
    public static void setPopulation(Population population) {
        INSTANCE.population = population;
    }

    public static InfluenceMap getInfluenceMap() {
        return INSTANCE.influence;
    }

    public static void setInfluenceMap(InfluenceMap influenceMap) {
        INSTANCE.influence = influenceMap;
    }

    public static Profile getProfile() {
        return INSTANCE.profile;
    }

    /**
     * 
     * @return the singleton instance of Orders
     */
    public static Orders getOrders() {
        return INSTANCE.orders;
    }

    public static void setOrders(Orders orders) {
        INSTANCE.orders = orders;
    }

    public static PathFinder getPathFinder() {
        return INSTANCE.pathFinder;
    }

    /**
     * 
     * @return timeout for initializing and setting up the bot on turn 0
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * 
     * @return timeout for a single game turn, starting with turn 1
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * 
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
    }

    /**
     * 
     * @return start time of the current turn.
     */
    public long getTurnStartTime() {
        return turnStartTime;
    }

    /**
     * 
     * @return how much time the bot has still has to take its turn before timing out
     */
    public int getTimeRemaining() {
        return turnTime - (int) (System.currentTimeMillis() - turnStartTime);
    }

    public String getTurnSummaryString() {
        return "-------------- Turn %s ------------ Ants: %s ----------- Missions: %s ----------------";
    }

    public Object[] getTurnSummaryParams() {
        return new Object[] { Ants.getAnts().getTurn(), Ants.getPopulation().getMyAnts().size(),
                Ants.getOrders().getMissions().size() };
    }

}
