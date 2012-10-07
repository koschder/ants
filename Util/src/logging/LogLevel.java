package logging;

public enum LogLevel {
    TRACE(3),
    DEBUG(2),
    INFO(1),
    ERROR(0);
    private int level;

    private LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
