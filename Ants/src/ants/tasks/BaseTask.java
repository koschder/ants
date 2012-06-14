package ants.tasks;

/**
 * Base implementation of a Task, mainly so Tasks that have no need for a setup method don't need to implement it.
 * 
 * @author kases1,kustl1
 * 
 */
public abstract class BaseTask implements Task {

    @Override
    public void setup() {
    }
}