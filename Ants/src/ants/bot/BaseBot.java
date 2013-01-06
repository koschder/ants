package ants.bot;

import influence.DefaultInfluenceMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;
import ants.entities.Ant;
import ants.profile.Profile;
import ants.state.Ants;
import ants.state.World;
import ants.tasks.Task;
import ants.util.LiveInfo;
import api.entities.Tile;

/**
 * Base Bot implementation. This serves as the base class for both our default implementation and the various test bots
 * we implemented during development.
 * 
 * @author kases1, kustl1
 */
public abstract class BaseBot extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.TURN);
    private static final Logger LOGGER_PERFORMANCE = LoggerFactory.getLogger(LogCategory.PERFORMANCE);
    private static final Logger LOGGER_STATISTICS = LoggerFactory.getLogger(LogCategory.STATISTICS);
    private static final Logger LOGGER_RESOURCES = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);

    protected String profile;

    protected Map<Task.Type, Task> tasks = new LinkedHashMap<Task.Type, Task>();

    // generating a history how many ants we have in each turn
    private List<Integer> statAntsAmountHistory = new ArrayList<Integer>();

    // generates a history how our influence to the enemies is
    // (myants/enemiesants)
    private List<Integer> statAntsInfluenceHistory = new ArrayList<Integer>();

    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2, int attackRadius2,
            int spawnRadius2) {
        Ants.getAnts().setup(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2,
                new Profile(profile));
    }

    @Override
    public void doTurn() {
        /*
         * This is the main loop of the bot. All the actual work is done in the tasks that are executed in the order
         * they are defined.
         */
        // write current turn number, ants amount into the log file.
        addTurnSummaryToLogfiles();
        // new calculation of the influence map
        calculateInfluence();
        // write some statistics about our population
        doStatistics();
        // initialize the task (abstract method) must be implemented by the inherited class
        initTasks();
        // execute all task (main work to do here)
        executeTask();
        // write all orders to the output stream
        Ants.getOrders().issueOrders();
        // log all ants which didn't get a job.
        logUnemployedAnts();
    }

    private void logUnemployedAnts() {
        final Collection<Ant> myUnemployedAnts = Ants.getPopulation().getMyUnemployedAnts();
        LOGGER_RESOURCES.info("Unemployed Ants (%s of %s): %s", myUnemployedAnts.size(), Ants.getPopulation()
                .getMyAnts().size(), myUnemployedAnts);
        for (Ant unemployed : myUnemployedAnts) {
            LiveInfo.liveInfo(Ants.getAnts().getTurn(), unemployed.getTile(), "Unemployed ant: %s",
                    unemployed.getTile());
        }
    }

    @Override
    protected void cleanup() {
        LOGGER.info("Cleaning up");
        LiveInfo.close();
        LoggerFactory.cleanup();
    }

    private void addTurnSummaryToLogfiles() {
        addTurnSummary(LogCategory.values());
        addTurnSummary(strategy.LogCategory.values());
        addTurnSummary(influence.LogCategory.values());
        addTurnSummary(pathfinder.LogCategory.values());
        // default log file:
        LOGGER.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());
    }

    private void addTurnSummary(logging.LogCategory[] categories) {
        for (logging.LogCategory logCat : categories) {
            if (logCat.useCustomLogFile()) {
                Logger customFileLogger = LoggerFactory.getLogger(logCat);
                customFileLogger.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());
            }
        }
    }

    private void executeTask() {
        for (Task task : tasks.values()) {
            long start = System.currentTimeMillis();
            int unemployed = Ants.getPopulation().getMyUnemployedAnts().size();
            LOGGER_PERFORMANCE.info("task started:: %s at %s", task.getClass().getSimpleName(), start);

            // execute the task
            task.perform();

            final int newlyEmployed = unemployed - Ants.getPopulation().getMyUnemployedAnts().size();
            final Integer maxAntsInt = Ants.getPopulation().getMaxAnts(task.getType());
            final String maxAnts = maxAntsInt < Integer.MAX_VALUE ? maxAntsInt.toString() : "unlimited";
            if (newlyEmployed < 0) {
                LOGGER_RESOURCES.info("Task %s released %s employed ants. (max: %s)", task.getClass().getSimpleName(),
                        -newlyEmployed, maxAnts);
            } else {
                LOGGER_RESOURCES.info("Task %s found jobs for %s of %s unemployed ants. (max: %s)", task.getClass()
                        .getSimpleName(), newlyEmployed, unemployed, maxAnts);
            }
            LOGGER_PERFORMANCE.info("task ended  :: %s, took %s ms", task.getClass().getSimpleName(),
                    System.currentTimeMillis() - start);
        }
    }

    private void calculateInfluence() {
        long start = System.currentTimeMillis();
        final World world = Ants.getWorld();
        if (Ants.getInfluenceMap() == null) {
            Ants.setInfluenceMap(new DefaultInfluenceMap(world, world.getViewRadius2(), world.getAttackRadius2()));
        } else {
            Ants.getInfluenceMap().update(world);
        }
        // printDebugInfluenceMap();
        LOGGER_PERFORMANCE.info("Calculating Influence took %s ms", System.currentTimeMillis() - start);
    }

    @SuppressWarnings("unused")
    private void printDebugInfluenceMap() {
        DefaultInfluenceMap dim = (DefaultInfluenceMap) Ants.getInfluenceMap();
        dim.printDebugMap(Ants.getAnts().getTurn(), Ants.getWorld(), getMyAntsTiles(), getEnemyAntsTiles());
    }

    private List<Tile> getMyAntsTiles() {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Ant ant : Ants.getPopulation().getMyAnts()) {
            tiles.add(ant.getTile());
        }
        return tiles;
    }

    private List<Tile> getEnemyAntsTiles() {
        List<Tile> tiles = new ArrayList<Tile>();
        for (Ant ant : Ants.getPopulation().getEnemyAnts()) {
            tiles.add(ant.getTile());
        }
        return tiles;
    }

    /**
     * Gathers statistics and periodically writes them to the log.
     */
    private void doStatistics() {

        statAntsAmountHistory.add(Ants.getPopulation().getMyAnts().size());
        statAntsInfluenceHistory
                .add((int) Math.round(Ants.getPopulation().getMyAnts().size()
                        / (Ants.getPopulation().getEnemyAnts().size() + Ants.getPopulation().getMyAnts().size() + 1.0)
                        * 100.0));

        // every 10 steps we write the statistic to the log
        if (Ants.getAnts().getTurn() % 10 == 0) {
            LOGGER_STATISTICS.info("Statistics: Influence history: %s", statAntsInfluenceHistory);
            LOGGER_STATISTICS.info("Statistics: Ants amount history: %s", statAntsAmountHistory);
        }
    }

    /**
     * Creates the {@link Task}s in order of importance if they are not yet created, and allows the tasks to perform
     * setup duties.
     */
    protected abstract void initTasks();

    @Override
    protected void doFinishTurn() {
        LOGGER_PERFORMANCE.info("Finished in %1$s ms with %2$s ms remaining.", System.currentTimeMillis()
                - Ants.getAnts().getTurnStartTime(), Ants.getAnts().getTimeRemaining());

    }
}