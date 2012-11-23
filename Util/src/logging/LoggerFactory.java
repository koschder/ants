package logging;

import java.util.HashMap;
import java.util.Map;

public class LoggerFactory {

    private static final LogFile logFile = new LogFile("logs/debug.log");
    private static final Map<LogCategory, LogFile> customLogFiles = new HashMap<LogCategory, LogFile>();

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
}
