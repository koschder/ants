package influence;

/**
 * log categories (implements for base LogCategory) to log the influence stuff
 * 
 * @author kases1, kustl1
 * 
 */
public enum LogCategory implements logging.LogCategory {

    /**
     * INFLUENCE is the only log category known in the influence packages
     */
    INFLUENCE(true);

    private boolean useCustomLogFile;

    private LogCategory(boolean useCustomLogFile) {
        this.useCustomLogFile = useCustomLogFile;
    }

    @Override
    public boolean useCustomLogFile() {
        return useCustomLogFile;
    }
}
