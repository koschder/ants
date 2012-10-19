package ants.strategy;

import java.util.HashMap;
import java.util.Map;

import ants.state.Ants;
import ants.tasks.Task;
import ants.tasks.Task.Type;
import api.InfluenceMap;

public class ResourceAllocator {

    private Map<Task.Type, Task> tasks = new HashMap<Task.Type, Task>();
    private InfluenceMap influence;

    public ResourceAllocator(Map<Type, Task> tasks, InfluenceMap influence) {
        this.tasks = tasks;
        this.influence = influence;

        tasks.get(Type.GATHER_FOOD).setMaxResources(40);
        tasks.get(Type.ATTACK_HILLS).setMaxResources(20);
        tasks.get(Type.COMBAT).setMaxResources(20);
        tasks.get(Type.EXPLORE).setMaxResources(20);
    }

    public void allocateResources() {

        if (Ants.getPopulation().getMyAnts().size() < 10) {

        }
        if (influence.getTotalInfluence(0) > influence.getTotalOpponentInfluence()) {
            incrementResources(Type.EXPLORE, 5, Type.GATHER_FOOD);
            incrementResources(Type.ATTACK_HILLS, 2, Type.GATHER_FOOD);
            incrementResources(Type.COMBAT, 2, Type.GATHER_FOOD);
        }

    }

    private void incrementResources(Type taskType, int increment, Type... tasksToDecrement) {
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
