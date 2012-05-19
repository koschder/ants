package ants.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import ants.entities.Tile;

public class Logger {
    public enum LogCategory {
        ATTACK_HILLS(INFO),
        CLEAR_HILL(INFO),
        COMBAT(INFO),
        DEFEND(INFO),
        EXCEPTION(INFO),
        EXECUTE_TASKS(DEBUG),
        EXECUTE_MISSIONS(INFO),
        EXPLORE(INFO),
        FOLLOW(INFO),
        FOOD(INFO),
        ORDERS(INFO),
        PATHFINDING(INFO),
        PERFORMANCE(INFO),
        SETUP(INFO),
        STATISTICS(INFO),
        TURN(INFO),
        CLUSTERING(DEBUG),
        CLUSTERING_Detail(INFO);

        int level;

        private LogCategory(int level) {
            this.level = level;
        }

    }

    private static boolean isFirst = true;
    private static final int DEBUG = 3;
    private static final int INFO = 2;
    private static final int ERROR = 1;
    @SuppressWarnings("unused")
    private static final int OFF = 0;

    private static PrintStream log;
    private static RandomAccessFile liveInfo;

    static {
        try {
            log = new PrintStream(new File("logs/debug.log"));
            String jsonfile = "logs/additionalInfo.json";
            File f = new File(jsonfile);
            if (f.exists())
                f.delete();
            liveInfo = new RandomAccessFile(new File(jsonfile), "rw");
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
        info(LogCategory.EXCEPTION, "%s Log %s ", error, logMsg);
        ex.printStackTrace(log);
    }

    public static void liveInfo(int turn, Tile tile, String message, Object... parameters) {
        try {
            String delimiter = "";
            if (isFirst) {
                liveInfo.write("{".getBytes());
                isFirst = false;
            } else {
                liveInfo.seek(0);
                delimiter = ",";
                liveInfo.seek(liveInfo.length() - 1); // this basically reads n bytes in the file
            }
            String msg = String.format(message, parameters).replace("\"", "'").replace("<r", "&lt;r")
                    .replace("\n", "<br/>");
            String sLiveInfo = String.format("%s\n\"%s#%s#%s\" : \"%s\"", delimiter, turn, tile.getRow(),
                    tile.getCol(), msg);
            // liveInfo.write("\n".getBytes());
            liveInfo.write(sLiveInfo.getBytes());
            liveInfo.write("}".getBytes());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logger.debug(LogCategory.EXCEPTION, e.getMessage());
        }

    }
}
