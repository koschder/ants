package ants;

public enum LogCategory implements logging.LogCategory {
    ATTACK_HILLS(true),
    CLEAR_HILL(false),
    COMBAT(false),
    DEFEND(false),
    EXCEPTION(false),
    EXECUTE_TASKS(false), 
    EXECUTE_MISSIONS(false),
    EXPLORE(false),
    FOLLOW(false),
    FOOD(true),
    ORDERS(false),
    CONCENTRATE(false),
    PERFORMANCE(true),
    SETUP(false),
    STATISTICS(false),
    TURN(false),
    RESOURCE_ALLOCATION(true),
    FLOCKING(false),
    SWARM(false),
    PATH_MISSION(false),
    DEFEND_HILL(false),
    ATTACK_HILLS_FLOCKED(false);
    private boolean useCustomLogFile;

    private LogCategory(boolean useCustomLogFile) {
        this.useCustomLogFile = useCustomLogFile;
    }

    @Override
    public boolean useCustomLogFile() {
        return useCustomLogFile;
    }
}
