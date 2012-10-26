package pathfinder;

public enum LogCategory implements logging.LogCategory {
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
