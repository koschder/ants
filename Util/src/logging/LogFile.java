package logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * this class represents a log file, logs are written with the append(..) function
 * 
 * @author kaeserst
 * 
 */
public class LogFile {

    private PrintStream log;

    /**
     * constructor, creating file if not exists.
     * 
     * @param filePath
     *            name an path of the file.
     */
    public LogFile(String filePath) {
        try {
            final File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            log = new PrintStream(file);
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
        log.println(String.format(message, parameters));
    }
}
