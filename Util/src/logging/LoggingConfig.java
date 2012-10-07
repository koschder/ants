package logging;

import java.util.HashMap;
import java.util.Map;

public class LoggingConfig {
    private static final Map<LogCategory, LogLevel> config = new HashMap<LogCategory, LogLevel>();

    public static void configure(LogCategory category, LogLevel level) {
        config.put(category, level);
    }

    public static boolean isEnabled(LogCategory category, LogLevel level) {
        LogLevel configuredLevel = config.get(category);
        return configuredLevel != null && configuredLevel.getLevel() >= level.getLevel();
    }
}
