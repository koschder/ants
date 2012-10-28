package ants.bot;

import java.io.IOException;

import ants.tasks.ClearHillTask;
import ants.tasks.Task;
import ants.tasks.Task.Type;

public class TroopBot extends BaseBot {

    public static void main(String[] args) throws IOException {
        new TroopBot().readSystemInput();
    }

    protected void initTasks() {
        if (tasks.isEmpty()) {
            tasks.put(Type.CLEAR_HILL, new ClearHillTask());
        }
        for (Task task : tasks.values()) {
            task.setup();
        }
    }
}
