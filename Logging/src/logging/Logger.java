package logging;

/**
 * Interface for a Logger
 * 
 * @author kases1, kustl1
 * 
 */
public interface Logger {
    /**
     * Log a message with TRACE LogLevel
     * 
     * @param message
     * @param parameters
     */
    public void trace(String message, Object... parameters);

    /**
     * Log a message with DEBUG LogLevel
     * 
     * @param message
     * @param parameters
     */
    public void debug(String message, Object... parameters);

    /**
     * Log a message with INFO LogLevel
     * 
     * @param message
     * @param parameters
     */
    public void info(String message, Object... parameters);

    /**
     * Log a message with ERROR LogLevel
     * 
     * @param message
     * @param parameters
     */
    public void error(String message, Object... parameters);
}
