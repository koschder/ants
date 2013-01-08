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
     * @return true if the {@link LogCategory} should use a separate file.
     */
    public boolean useCustomLogFile();
}
