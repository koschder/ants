package logging;

public class DefaultLogger implements Logger {

    private LogCategory category;
    private LogFile logFile;

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
