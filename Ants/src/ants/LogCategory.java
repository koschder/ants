package ants;

public enum LogCategory implements logging.LogCategory {
    ATTACK_HILLS(false),
    CLEAR_HILL(false),
    COMBAT(false),
    DEFEND(false),
    EXCEPTION(false),
    EXECUTE_TASKS(false),
    EXECUTE_MISSIONS(false),
    EXPLORE(false),
    FOLLOW(false),
    FOOD(false),
    ORDERS(false),
    PERFORMANCE(false),
    SETUP(false),
    STATISTICS(false),
    TURN(false),
    RESOURCE_ALLOCATION(true);
    private boolean useCustomLogFile;

    private LogCategory(boolean useCustomLogFile) {
        this.useCustomLogFile = useCustomLogFile;
    }

    @Override
    public boolean useCustomLogFile() {
        return useCustomLogFile;
    }
}
