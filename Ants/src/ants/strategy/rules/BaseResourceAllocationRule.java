package ants.strategy.rules;

import java.util.Map;

import ants.tasks.Task;
import ants.tasks.Task.Type;

public abstract class BaseResourceAllocationRule implements ResourceAllocationRule {
    protected void incrementResources(Map<Task.Type, Task> tasks, Type taskType, int increment,
            Type... tasksToDecrement) {
        if ((increment % tasksToDecrement.length) != 0)
            throw new IllegalArgumentException("Cannot evenly distribute the increment");
        final int decrementPerTask = increment / tasksToDecrement.length;
        for (Type type : tasksToDecrement) {
            final Task taskToDecrement = tasks.get(type);
            int maxResources = taskToDecrement.getMaxResources();
            int newMax = maxResources - decrementPerTask;
            if (newMax < 0) {
                increment = increment + newMax;
                newMax = 0;
            }
            taskToDecrement.setMaxResources(newMax);
        }
        final Task taskToIncrement = tasks.get(taskType);
        taskToIncrement.setMaxResources(taskToIncrement.getMaxResources() + increment);
    }

}
