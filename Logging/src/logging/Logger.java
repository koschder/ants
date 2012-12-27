package logging;

/**
 * interface of logger, the methods: trace, debug, info, and error must be implemented to by using this interface.
 * 
 * @author kaeserst, kustl1
 * 
 */
public interface Logger {
    public void trace(String message, Object... parameters);

    public void debug(String message, Object... parameters);

    public void info(String message, Object... parameters);

    public void error(String message, Object... parameters);
}
