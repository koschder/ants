package logging;

/**
 * this class is the default implementation of our logging functionality it is possible to log in different
 * {@link LogLevel} and categories and even in a different file for each category.
 * 
 * @author kases1, kustl1
 * 
 */
public class DefaultLogger implements Logger {

    private LogCategory category;
    private LogFile logFile;

    /**
     * Default constructor
     * 
     * @param category
     * @param logFile
     */
    public DefaultLogger(LogCategory category, LogFile logFile) {
        this.category = category;
        this.logFile = logFile;
    }

    @Override
    public void trace(String message, Object... parameters) {
        if (LoggingConfig.isEnabled(category, LogLevel.TRACE))
            log(message, parameters);

    }

    @Override
    public void debug(String message, Object... parameters) {
        if (LoggingConfig.isEnabled(category, LogLevel.DEBUG))
            log(message, parameters);
    }

    @Override
    public void info(String message, Object... parameters) {
        if (LoggingConfig.isEnabled(category, LogLevel.INFO))
            log(message, parameters);
    }

    @Override
    public void error(String message, Object... parameters) {
        if (LoggingConfig.isEnabled(category, LogLevel.ERROR))
            log(message, parameters);

    }

    protected void log(String message, Object... parameters) {
        logFile.append(message, parameters);
    }

}
