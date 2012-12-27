package logging;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * LoggerFactory uses the factory pattern providing a Logger for each category
 * 
 * @author kaeserst,kustl1
 * 
 */
public class LoggerFactory {

    private static String baseDir = "logs/";
    /**
     * default log path/file
     */
    private static final LogFile logFile = new LogFile(null);
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
                file = new LogFile(category);
                customLogFiles.put(category, file);
            }
            return new DefaultLogger(category, file);
        }
        return new DefaultLogger(category, logFile);
    }

    public static void setProfile(String profile) {
        baseDir += profile == null ? "" : profile + "/";
        new File(baseDir).mkdirs();
    }

    public static String getBaseDir() {
        return baseDir;
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
