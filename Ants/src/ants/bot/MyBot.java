package ants.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import ants.util.Logger;
import ants.util.Logger.LogCategory;

/**
 * Bot implementation. This was originally based on the sample bot from the starter package, but the implementation is
 * completely different, although the hierarchy is still the same.
 * 
 * @author kases1,kustl1
 */
public class MyBot extends Bot {
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
        new MyBot().readSystemInput();
    }

    private List<Task> tasks = new ArrayList<Task>();

    // generating a history how many ants we have in each turn
    private List<Integer> statAntsAmountHistory = new ArrayList<Integer>();

    // generates a history how our influence to the enemies is
    // (myants/enemiesants)
    private List<Integer> statAntsInfluenceHistory = new ArrayList<Integer>();

    @Override
    public void doTurn() {
        Logger.info(LogCategory.TURN,
                "------------ Turn %s ----------- Ants: %s --------- Missions: %s ----------------------------", Ants
                        .getAnts().getTurn(), Ants.getPopulation().getMyAnts().size(), Ants.getOrders().getMissions()
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
            Logger.info(LogCategory.PERFORMANCE, "task started:: %s at %s", task.getClass().getSimpleName(), start);
            task.perform();
            Logger.debug(LogCategory.EXECUTE_TASKS, "Task %s found jobs for %s of %s unemployed ants", task.getClass()
                    .getSimpleName(), unemployed - Ants.getPopulation().getMyUnemployedAnts().size(), unemployed);
            Logger.info(LogCategory.PERFORMANCE, "task ended  :: %s, took %s ms", task.getClass().getSimpleName(),
                    System.currentTimeMillis() - start);
        }
        final Collection<Ant> myUnemployedAnts = Ants.getPopulation().getMyUnemployedAnts();
        Logger.debug(LogCategory.EXECUTE_TASKS, "Unemployed Ants (%s total): %s", myUnemployedAnts.size(),
                myUnemployedAnts);
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
            Logger.info(LogCategory.STATISTICS, "Statistics: Influence history: %s", statAntsInfluenceHistory);
            Logger.info(LogCategory.STATISTICS, "Statistics: Ants amount history: %s", statAntsAmountHistory);
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
        Logger.info(LogCategory.TURN, "Finished in %1$s ms with %2$s ms remaining.", System.currentTimeMillis()
                - Ants.getAnts().getTurnStartTime(), Ants.getAnts().getTimeRemaining());

    }
}