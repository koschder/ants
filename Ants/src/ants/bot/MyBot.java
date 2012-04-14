package ants.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ants.state.Ants;
import ants.tasks.AttackHillsTask;
import ants.tasks.ClearHillTask;
import ants.tasks.CombatTask;
import ants.tasks.ExploreTask;
import ants.tasks.FollowTask;
import ants.tasks.GatherFoodTask;
import ants.tasks.MissionTask;
import ants.tasks.Task;
import ants.util.Logger;
import ants.util.Logger.LogCategory;


/**
 * Starter bot implementation.
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
        for (Task task : tasks) {
            long start = System.currentTimeMillis();
            Logger.info(LogCategory.PERFORMANCE, "task started:: %s at %s", task.getClass(), start);
            task.perform();
            Logger.info(LogCategory.PERFORMANCE, "task ended  :: %s, took %s ms", task.getClass(),
                    System.currentTimeMillis() - start);
        }
        Ants.getOrders().issueOrders();
    }

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

    private void initTasks() {
        if (tasks.isEmpty()) {
            tasks.add(new MissionTask());
            tasks.add(new GatherFoodTask());
            tasks.add(new AttackHillsTask());
            tasks.add(new CombatTask());
            tasks.add(new FollowTask());
            tasks.add(new ExploreTask());
            tasks.add(new ClearHillTask());
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