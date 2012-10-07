package logging;

public class LoggerFactory {

    private static final LogFile logFile = new LogFile("logs/debug1.log");

    public static Logger getLogger(LogCategory category) {
        return new DefaultLogger(category, logFile);
    }
}
