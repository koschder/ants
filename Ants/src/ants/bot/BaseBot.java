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
import ants.state.Ants;
import ants.state.World;
import ants.tasks.Task;
import ants.util.LiveInfo;
import api.entities.Tile;

/**
 * Bot implementation. This was originally based on the sample bot from the starter package, but the implementation is
 * completely different, although the hierarchy is still the same.
 * 
 * @author kases1,kustl1
 */
public abstract class BaseBot extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.TURN);
    private static final Logger LOGGER_PERFORMANCE = LoggerFactory.getLogger(LogCategory.PERFORMANCE);
    private static final Logger LOGGER_STATISTICS = LoggerFactory.getLogger(LogCategory.STATISTICS);
    private static final Logger LOGGER_RESOURCES = LoggerFactory.getLogger(LogCategory.RESOURCE_ALLOCATION);

    protected Map<Task.Type, Task> tasks = new LinkedHashMap<Task.Type, Task>();

    // generating a history how many ants we have in each turn
    private List<Integer> statAntsAmountHistory = new ArrayList<Integer>();

    // generates a history how our influence to the enemies is
    // (myants/enemiesants)
    private List<Integer> statAntsInfluenceHistory = new ArrayList<Integer>();

    @Override
    public void doTurn() {
        addTurnSummaryToLogfiles();
        LOGGER.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());

        LOGGER.info("enemy_hills visible: %s", Ants.getWorld().getEnemyHills(), Ants.getWorld());
        calculateInfluence();
        initTasks();
        doStatistics();
        /*
         * This is the main loop of the bot. All the actual work is done in the tasks that are executed in the order
         * they are defined.
         */
        executeTask();
        final Collection<Ant> myUnemployedAnts = Ants.getPopulation().getMyUnemployedAnts();
        LOGGER_RESOURCES.info("Unemployed Ants (%s of %s): %s", myUnemployedAnts.size(), Ants.getPopulation()
                .getMyAnts().size(), myUnemployedAnts);
        Ants.getOrders().issueOrders();
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
        for (LogCategory logCat : LogCategory.values()) {
            addTurnSummary(logCat);
        }
        for (strategy.LogCategory logCat : strategy.LogCategory.values()) {
            addTurnSummary(logCat);
        }
        for (influence.LogCategory logCat : influence.LogCategory.values()) {
            addTurnSummary(logCat);
        }
        for (pathfinder.LogCategory logCat : pathfinder.LogCategory.values()) {
            addTurnSummary(logCat);
        }
    }

    private void addTurnSummary(logging.LogCategory logCat) {
        if (logCat.useCustomLogFile()) {
            Logger customFileLogger = LoggerFactory.getLogger(logCat);
            customFileLogger.info(Ants.getAnts().getTurnSummaryString(), Ants.getAnts().getTurnSummaryParams());
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
            // Ants.getWorld().printIlk();
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