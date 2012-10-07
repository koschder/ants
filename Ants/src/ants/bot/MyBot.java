package ants.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import logging.LogLevel;
import logging.Logger;
import logging.LoggerFactory;
import logging.LoggingConfig;
import ants.LogCategory;
import ants.entities.Ant;
import ants.state.Ants;
import ants.tasks.AttackHillsTask;
import ants.tasks.ClearHillTask;
import ants.tasks.ClusteringTask;
import ants.tasks.CombatTask;
import ants.tasks.ExploreTask;
import ants.tasks.FollowTask;
import ants.tasks.GatherFoodTask;
import ants.tasks.MissionTask;
import ants.tasks.Task;

/**
 * Bot implementation. This was originally based on the sample bot from the starter package, but the implementation is
 * completely different, although the hierarchy is still the same.
 * 
 * @author kases1,kustl1
 */
public class MyBot extends Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.TURN);
    private static final Logger LOGGER_PERFORMANCE = LoggerFactory.getLogger(LogCategory.PERFORMANCE);
    private static final Logger LOGGER_TASKS = LoggerFactory.getLogger(LogCategory.EXECUTE_TASKS);
    private static final Logger LOGGER_STATISTICS = LoggerFactory.getLogger(LogCategory.STATISTICS);

    /**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args
     *            command line arguments
     * 
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        initLogging();
        new MyBot().readSystemInput();
    }

    private static void initLogging() {
        LoggingConfig.configure(LogCategory.ATTACK_HILLS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.CLEAR_HILL, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.COMBAT, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.DEFEND, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXCEPTION, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXECUTE_TASKS, LogLevel.DEBUG);
        LoggingConfig.configure(LogCategory.EXECUTE_MISSIONS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXPLORE, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.FOLLOW, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.FOOD, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.ORDERS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.PERFORMANCE, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.SETUP, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.STATISTICS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.TURN, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.PATHFINDING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERED_ASTAR, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.HPASTAR, LogLevel.INFO);
    }

    private List<Task> tasks = new ArrayList<Task>();

    // generating a history how many ants we have in each turn
    private List<Integer> statAntsAmountHistory = new ArrayList<Integer>();

    // generates a history how our influence to the enemies is
    // (myants/enemiesants)
    private List<Integer> statAntsInfluenceHistory = new ArrayList<Integer>();

    @Override
    public void doTurn() {
        LOGGER.info("------------ Turn %s ----------- Ants: %s --------- Missions: %s ----------------------------",
                Ants.getAnts().getTurn(), Ants.getPopulation().getMyAnts().size(), Ants.getOrders().getMissions()
                        .size());
        initTasks();
        doStatistics();
        /*
         * This is the main loop of the bot. All the actual work is done in the tasks that are executed in the order
         * they are defined.
         */
        for (Task task : tasks) {
            long start = System.currentTimeMillis();
            int unemployed = Ants.getPopulation().getMyUnemployedAnts().size();
            LOGGER_PERFORMANCE.info("task started:: %s at %s", task.getClass().getSimpleName(), start);
            task.perform();
            LOGGER_TASKS.debug("Task %s found jobs for %s of %s unemployed ants", task.getClass().getSimpleName(),
                    unemployed - Ants.getPopulation().getMyUnemployedAnts().size(), unemployed);
            LOGGER_PERFORMANCE.info("task ended  :: %s, took %s ms", task.getClass().getSimpleName(),
                    System.currentTimeMillis() - start);
        }
        final Collection<Ant> myUnemployedAnts = Ants.getPopulation().getMyUnemployedAnts();
        LOGGER_TASKS.debug("Unemployed Ants (%s total): %s", myUnemployedAnts.size(), myUnemployedAnts);
        Ants.getOrders().issueOrders();
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
    private void initTasks() {
        if (tasks.isEmpty()) {
            tasks.add(new MissionTask());
            tasks.add(new GatherFoodTask());
            tasks.add(new AttackHillsTask());
            tasks.add(new CombatTask());
            tasks.add(new ExploreTask());
            tasks.add(new FollowTask());
            tasks.add(new ClearHillTask());
            tasks.add(new ClusteringTask());
        }
        for (Task task : tasks) {
            task.setup();
        }
    }

    @Override
    protected void doFinishTurn() {
        LOGGER.info("Finished in %1$s ms with %2$s ms remaining.", System.currentTimeMillis()
                - Ants.getAnts().getTurnStartTime(), Ants.getAnts().getTimeRemaining());

    }
}