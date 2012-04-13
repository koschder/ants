package starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import starter.Logger.LogCategory;
import starter.tasks.AttackHillsTask;
import starter.tasks.ClearHillTask;
import starter.tasks.CombatTask;
import starter.tasks.ExploreTask;
import starter.tasks.FollowTask;
import starter.tasks.GatherFoodTask;
import starter.tasks.MissionTask;
import starter.tasks.Task;

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

    private static int turn = 0;

    private List<Task> tasks = new ArrayList<Task>();

    // generating a history how many ants we have in each turn
    private List<Integer> statAntsAmountHistory = new ArrayList<Integer>();

    // generates a history how our influence to the enemies is
    // (myants/enemiesants)
    private List<Integer> statAntsInfluenceHistory = new ArrayList<Integer>();

    @Override
    public void doTurn() {
        Logger.log(LogCategory.TURN,
                "------------ Turn %s ----------- Ants: %s --------- Missions: %s ----------------------------",
                turn++, getAnts().getMyAnts().size(), getAnts().getMissions().size());
        getAnts().initOrders();
        initTasks();
        doStatistics();
        for (Task task : tasks) {
            long start = System.currentTimeMillis();
            Logger.log(LogCategory.PERFORMANCE, "task started:: %s at %s", task.getClass(), start);
            task.perform();
            Logger.log(LogCategory.PERFORMANCE, "task ended  :: %s, took %s ms", task.getClass(),
                    System.currentTimeMillis() - start);
        }
        getAnts().issueOrders();
    }

    private void doStatistics() {

        statAntsAmountHistory.add(getAnts().getMyAnts().size());
        statAntsInfluenceHistory.add((int) Math.round(getAnts().getMyAnts().size()
                / (getAnts().getEnemyAnts().size() + getAnts().getMyAnts().size() + 1.0) * 100.0));

        // every 10 steps we write the statistic to the log
        if (turn % 10 == 0) {
            Logger.log(LogCategory.STATISTICS, "Statistics: Influence history: %s", statAntsInfluenceHistory);
            Logger.log(LogCategory.STATISTICS, "Statistics: Ants amount history: %s", statAntsAmountHistory);
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
            task.setup(getAnts());
        }
    }

    @Override
    protected void doFinishTurn() {
        Logger.log(LogCategory.TURN, "Finished in %1$s ms with %2$s ms remaining.", System.currentTimeMillis()
                - getAnts().getTurnStartTime(), getAnts().getTimeRemaining());

    }
}