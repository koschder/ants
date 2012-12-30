package logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * this class represents a log file, logs are written with the append(..) function
 * 
 * @author kases1, kustl1
 * 
 */
public class LogFile {

    private PrintStream log;
    private boolean initialized = false;
    private LogCategory category;

    /**
     * constructor, creating file if not exists.
     * 
     * @param filePath
     *            name an path of the file.
     */
    public LogFile(LogCategory category) {
        this.category = category;
    }

    private void initLogFile() {
        try {
            final String name = category == null ? "debug" : category.toString();
            final File file = new File(LoggerFactory.getBaseDir() + name + ".log");
            if (!file.exists())
                file.createNewFile();
            log = new PrintStream(file);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * append a log line to the file
     * 
     * @param message
     * @param parameters
     */
    public void append(String message, Object... parameters) {
        if (!initialized)
            initLogFile();
        log.println(String.format(message, parameters));
    }

    /**
     * Close the underlying stream
     */
    public void close() {
        log.close();
    }
}
