package logging;

import java.util.HashMap;
import java.util.Map;

/**
 * LoggerFactory uses the factory pattern providing a Logger for each category
 * 
 * @author kaeserst,kustl1
 * 
 */
public class LoggerFactory {

    /**
     * default log path/file
     */
    private static final LogFile logFile = new LogFile("logs/debug.log");
    /**
     * stores all already created files each log category
     */
    private static final Map<LogCategory, LogFile> customLogFiles = new HashMap<LogCategory, LogFile>();

    /**
     * returns the logger
     * 
     * @param category
     *            of the logger to be return
     * @return
     */
    public static Logger getLogger(LogCategory category) {
        if (category.useCustomLogFile()) {
            LogFile file = customLogFiles.get(category);
            if (file == null) {
                file = new LogFile("logs/" + category.toString() + ".log");
                customLogFiles.put(category, file);
            }
            return new DefaultLogger(category, file);
        }
        return new DefaultLogger(category, logFile);
    }

    /**
     * Close all log files
     */
    public static void cleanup() {
        logFile.close();
        for (LogFile log : customLogFiles.values()) {
            log.close();
        }
    }
}
