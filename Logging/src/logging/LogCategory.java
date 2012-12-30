package logging;

/**
 * Interface for log category
 * 
 * @author kases1, kustl1
 * 
 */
public interface LogCategory {
    /**
     * 
     * @return true if the logcategory uses its own file.
     */
    public boolean useCustomLogFile();
}
