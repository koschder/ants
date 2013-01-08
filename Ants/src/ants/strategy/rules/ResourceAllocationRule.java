package ants.strategy.rules;

/**
 * This interface must be implemented by rules for allocating resources.
 * 
 * @author kases1, kustl1
 * 
 */
public interface ResourceAllocationRule {
    /**
     * allocate resources according to the rule's logic
     */
    public void allocateResources();
}
