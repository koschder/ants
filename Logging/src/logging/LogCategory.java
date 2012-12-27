package logging;

/**
 * Interface for log category
 * 
 * @author kaeserst, kustl1
 * 
 */
public interface LogCategory {
    /**
     * 
     * @return true if the logcategory uses its own file.
     */
    public boolean useCustomLogFile();
}