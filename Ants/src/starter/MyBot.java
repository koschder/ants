package starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import starter.tasks.AttackHillsTask;
import starter.tasks.ClearHillTask;
import starter.tasks.ExploreTask;
import starter.tasks.GatherFoodTask;
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

    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

    private List<Task> tasks = new ArrayList<Task>();

    @Override
    public void doTurn() {
        Logger.log("Turn %1$d", ++turn);
        Logger.log("Ants: %1$d", getAnts().getMyAnts().size());
        initOrders();
        initTasks();
        for (Task task : tasks) {
            task.perform();
        }
    }

    private void initOrders() {
        orders.clear();
        // prevent stepping on own hill
        for (Tile myHill : getAnts().getMyHills()) {
            orders.put(myHill, null);
        }
    }

    private void initTasks() {
        if (tasks.isEmpty()) {
            tasks.add(new GatherFoodTask());
            tasks.add(new AttackHillsTask());
            tasks.add(new ExploreTask());
            tasks.add(new ClearHillTask());
        }
        for (Task task : tasks) {
            task.setup(getAnts(), orders);
        }
    }

    @Override
    protected void doFinishTurn() {
        Logger.log("Finished in %1$s ms with %2$s ms remaining.", System.currentTimeMillis()
                - getAnts().getTurnStartTime(), getAnts().getTimeRemaining());

    }
}