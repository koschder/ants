package logging;

public interface Logger {
    public void trace(String message, Object... parameters);

    public void debug(String message, Object... parameters);

    public void info(String message, Object... parameters);

    public void error(String message, Object... parameters);
}
