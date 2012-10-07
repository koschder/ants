package logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class LogFile {

    private PrintStream log;

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

    public void append(String message, Object... parameters) {
        log.println(String.format(message, parameters));
    }
}
