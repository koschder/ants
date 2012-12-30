package ants.bot.impl;

import java.io.IOException;

import logging.LogLevel;
import logging.LoggerFactory;
import logging.LoggingConfig;
import ants.LogCategory;
import ants.bot.BaseBot;
import ants.state.Ants;
import ants.strategy.ResourceAllocator;
import ants.tasks.AttackHillsTask;
import ants.tasks.ClearHillTask;
import ants.tasks.ClusteringTask;
import ants.tasks.DefendHillTask;
import ants.tasks.ExploreTask;
import ants.tasks.GatherFoodTask;
import ants.tasks.MissionTask;
import ants.tasks.RavTask;
import ants.tasks.Task;
import ants.tasks.Task.Type;

/**
 * Bot implementation. This was originally based on the sample bot from the starter package, but the implementation is
 * completely different, although the hierarchy is still the same.
 * 
 * @author kases1, kustl1
 */
public class MyBot extends BaseBot {

    public MyBot(String profile) {
        this.profile = profile;
    }

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
        // we only support one arg, and that is assumed to be the profile name
        String profile = args.length == 1 ? args[0] : null;
        initLogging(profile);
        new MyBot(profile).readSystemInput();
    }

    private static void initLogging(String profile) {
        LoggerFactory.setProfile(profile);
        LoggingConfig.configure(LogCategory.ATTACK_HILLS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.CLEAR_HILL, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.COMBAT, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.DEFEND, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.DEFEND_HILL, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXCEPTION, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXECUTE_TASKS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXECUTE_MISSIONS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.EXPLORE, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.FOLLOW, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.FOOD, LogLevel.DEBUG);
        LoggingConfig.configure(LogCategory.PATH_MISSION, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.ORDERS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.PERFORMANCE, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.SETUP, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.STATISTICS, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.TURN, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.FLOCKING, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.WORLD, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.RESOURCE_ALLOCATION, LogLevel.INFO);
        LoggingConfig.configure(LogCategory.CONCENTRATE, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.PATHFINDING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.BFS, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERING, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.CLUSTERED_ASTAR, LogLevel.INFO);
        LoggingConfig.configure(pathfinder.LogCategory.HPASTAR, LogLevel.INFO);
        LoggingConfig.configure(influence.LogCategory.INFLUENCE, LogLevel.INFO);
        LoggingConfig.configure(strategy.LogCategory.COMBAT_POSITIONING, LogLevel.DEBUG);
    }

    protected void initTasks() {
        if (tasks.isEmpty()) {
            tasks.put(Type.MISSION, new MissionTask());
            tasks.put(Type.GATHER_FOOD, new GatherFoodTask());
            tasks.put(Type.ATTACK_HILLS, new AttackHillsTask());
            tasks.put(Type.DEFEND_HILL, new DefendHillTask());
            tasks.put(Type.EXPLORE, new ExploreTask());
            tasks.put(Type.CLEAR_HILL, new ClearHillTask());
            tasks.put(Type.COMBAT, new RavTask());
            tasks.put(Type.CLUSTERING, new ClusteringTask());
        }
        new ResourceAllocator(Ants.getInfluenceMap()).allocateResources();
        for (Task task : tasks.values()) {
            task.setup();
        }
    }
}