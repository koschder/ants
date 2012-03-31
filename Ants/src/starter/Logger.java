package starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger {
    private static PrintStream log;
    static {
        try {
            log = new PrintStream(new File("logs/debug.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message, Object... parameters) {
        log.println(String.format(message, parameters));
    }

    public static void exception(String message, Exception ex, Object... parameters) {
        String logMsg = String.format(message, parameters);
        String error = String.format("EXCEPTION: %s stacktrace:  ", ex);
        log("%s Log %s ", error, logMsg);
        ex.printStackTrace(log);
    }
}
