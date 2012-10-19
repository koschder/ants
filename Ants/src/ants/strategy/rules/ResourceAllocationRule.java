package ants.strategy.rules;

import java.util.Map;

import ants.tasks.Task;

public interface ResourceAllocationRule {
    public void allocateResources(Map<Task.Type, Task> tasks);
}
