package ants.bot;

import java.io.IOException;

import logging.LogLevel;
import logging.LoggingConfig;
import ants.LogCategory;
import ants.tasks.MissionTask;
import ants.tasks.Task;
import ants.tasks.Task.Type;
import ants.tasks.TroopTask;

public class TroopBot extends BaseBot {

    public static void main(String[] args) throws IOException {
        initLogging();
        new TroopBot().readSystemInput();
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
        LoggingConfig.configure(LogCategory.TROOP, LogLevel.DEBUG);
        LoggingConfig.configure(LogCategory.ORDERS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.PERFORMANCE, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.SETUP, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.STATISTICS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.TURN, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.RESOURCE_ALLOCATION, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.PATHFINDING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERED_ASTAR, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.HPASTAR, LogLevel.INFO);
    }

    protected void initTasks() {
        if (tasks.isEmpty()) {
            tasks.put(Type.MISSION, new MissionTask());
            tasks.put(Type.TROOP_ANTS, new TroopTask());
        }
        for (Task task : tasks.values()) {
            task.setup();
        }
    }
}
