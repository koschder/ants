package logging;

import java.util.HashMap;
import java.util.Map;

/**
 * With this class we determine if we have to write a log according to its category and log level.
 * 
 * @author kases1, kustl1
 * 
 */
public class LoggingConfig {
    private static final Map<LogCategory, LogLevel> config = new HashMap<LogCategory, LogLevel>();

    /**
     * store the minimum log level for each category
     * 
     * @param category
     * @param level
     */
    public static void configure(LogCategory category, LogLevel level) {
        config.put(category, level);
    }

    /**
     * checks if we write the log.
     * 
     * @param category
     * @param level
     * @return true if we should write the log
     */
    public static boolean isEnabled(LogCategory category, LogLevel level) {
        LogLevel configuredLevel = config.get(category);
        return configuredLevel != null && configuredLevel.getLevel() >= level.getLevel();
    }
}
