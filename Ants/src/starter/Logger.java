package starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger {
    public enum LogCategory {
        ATTACK_HILLS(INFO),
        CLEAR_HILL(INFO),
        COMBAT(INFO),
        DEFEND(INFO),
        ERROR(INFO),
        EXECUTE_TASKS(INFO),
        EXECUTE_MISSIONS(INFO),
        EXPLORE(INFO),
        FOLLOW(INFO),
        FOOD(INFO),
        ORDERS(INFO),
        PATHFINDING(OFF),
        PERFORMANCE(INFO),
        SETUP(INFO),
        STATISTICS(INFO),
        TURN(INFO);

        int level;

        private LogCategory(int level) {
            this.level = level;
        }

    }

    private static final int DEBUG = 3;
    private static final int INFO = 2;
    private static final int ERROR = 1;
    private static final int OFF = 0;

    private static PrintStream log;
    private static PrintStream liveInfo;

    static {
        try {
            log = new PrintStream(new File("logs/debug.log"));
            liveInfo = new PrintStream(new File("logs/additionalInfo0.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void debug(LogCategory category, String message, Object... parameters) {
        log(DEBUG, category, message, parameters);
    }

    public static void info(LogCategory category, String message, Object... parameters) {
        log(INFO, category, message, parameters);
    }

    public static void error(LogCategory category, String message, Object... parameters) {
        log(ERROR, category, message, parameters);
    }

    private static void log(int logLevel, LogCategory category, String message, Object... parameters) {
        if (category.level >= logLevel)
            log.println(String.format(message, parameters));
    }

    public static void exception(String message, Exception ex, Object... parameters) {
        String logMsg = String.format(message, parameters);
        String error = String.format("EXCEPTION: %s stacktrace:  ", ex);
        info(LogCategory.ERROR, "%s Log %s ", error, logMsg);
        ex.printStackTrace(log);
    }

    public static void liveInfo(int turn, Tile tile, String message, Object... parameters) {
        String msg = String.format(message, parameters).replace("\"", "'");
        String sLiveInfo = String.format("\"%s#%s#%s\": \"%s\",", turn, tile.getRow(), tile.getCol(), msg);
        liveInfo.println(sLiveInfo);
    }

}
