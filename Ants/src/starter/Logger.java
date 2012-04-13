package starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Logger {
    public enum LogCategory {
        ATTACK_HILLS,
        CLEAR_HILL,
        COMBAT,
        DEFEND,
        ERROR,
        EXECUTE_TASKS,
        EXECUTE_MISSIONS,
        EXPLORE,
        FOLLOW,
        FOOD,
        ORDERS,
        PATHFINDING,
        PERFORMANCE,
        SETUP,
        STATISTICS,
        TRACE,
        TURN;
    }

    private static Set<LogCategory> enabledCategories;
    private static PrintStream log;

    private static void configure() {
        enabledCategories = new HashSet<Logger.LogCategory>();
        enabledCategories.add(LogCategory.ATTACK_HILLS);
        enabledCategories.add(LogCategory.CLEAR_HILL);
        enabledCategories.add(LogCategory.COMBAT);
        enabledCategories.add(LogCategory.DEFEND);
        enabledCategories.add(LogCategory.ERROR);
        enabledCategories.add(LogCategory.EXECUTE_TASKS);
        enabledCategories.add(LogCategory.EXECUTE_MISSIONS);
        enabledCategories.add(LogCategory.EXPLORE);
        enabledCategories.add(LogCategory.FOLLOW);
        enabledCategories.add(LogCategory.FOOD);
        // enabledCategories.add(LogCategory.ORDERS);
        // enabledCategories.add(LogCategory.PATHFINDING);
        enabledCategories.add(LogCategory.PERFORMANCE);
        enabledCategories.add(LogCategory.SETUP);
        enabledCategories.add(LogCategory.STATISTICS);
        // enabledCategories.add(LogCategory.TRACE);
        enabledCategories.add(LogCategory.TURN);
    }

    static {
        configure();
        try {
            log = new PrintStream(new File("logs/debug.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log(LogCategory category, String message, Object... parameters) {
        if (enabledCategories.contains(category))
            log.println(String.format(message, parameters));
    }

    public static void exception(String message, Exception ex, Object... parameters) {
        String logMsg = String.format(message, parameters);
        String error = String.format("EXCEPTION: %s stacktrace:  ", ex);
        log(LogCategory.ERROR, "%s Log %s ", error, logMsg);
        ex.printStackTrace(log);
    }
}
