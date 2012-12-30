package strategy;

/**
 * log categories (implements for base LogCategory) to log the strategy stuff
 * 
 * @author kaeserst, kustl1
 * 
 */
public enum LogCategory implements logging.LogCategory {

    /**
     * COMBAT_POSITIONING is the only log category known in the strategy packages
     */
    COMBAT_POSITIONING(true);
    private boolean useCustomLogFile;

    private LogCategory(boolean useCustomLogFile) {
        this.useCustomLogFile = useCustomLogFile;
    }

    @Override
    public boolean useCustomLogFile() {
        return useCustomLogFile;
    }
}
