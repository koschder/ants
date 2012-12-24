package pathfinder;

/**
 * log categories (implements for base LogCategory) to log the pathfinder stuff
 * 
 * @author kaeserst, kustl1
 * 
 */
public enum LogCategory implements logging.LogCategory {
    /**
     * each search type has its own log category
     */
    BFS(true),
    CLUSTERING(false),
    PATHFINDING(false),
    CLUSTERED_ASTAR(false),
    HPASTAR(false);
    private boolean useCustomLogFile;

    private LogCategory(boolean useCustomLogFile) {
        this.useCustomLogFile = useCustomLogFile;
    }

    @Override
    public boolean useCustomLogFile() {
        return useCustomLogFile;
    }
}
