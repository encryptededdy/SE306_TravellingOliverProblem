package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.Set;

/*
This class describes a section of a schedule for a given input graph
 */
public class Schedule {
    private Set<Node> addedNodes;
    private Set<Node> unAddedNodes;
    private Set<Node> availableNodes;

    private ScheduledProcessor[] processors;

    // Constructor
    public Schedule(int processorCount, Set<Node> availableNodes, Set<Node> allNodes) {
        unAddedNodes = allNodes;
        this.availableNodes = availableNodes;

        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    // Returns all the processors
    public ScheduledProcessor[] getProcessors() {
        return processors;
    }

    // Adds a node to a given processor
    public boolean addToSchedule(Node node, ScheduledProcessor processor) {
        return false;
    }

    // Gets the processor that has the earliest start time for a given node
    public ScheduledProcessor getEarliestProcessor(Node node) {
        return null;
    }

    // Gets the earliest start time for a node and a specified processor
    private int getEarliestTimeForProcessor(Node node, ScheduledProcessor processor) {
        return 0;
    }

    // Returns all nodes that have been added to the schedule
    public Set<Node> getAddedNodes() {
        return addedNodes;
    }

    // Returns all nodes that have not been added to the schedule
    public Set<Node> getUnAddedNodes() {
        return unAddedNodes;
    }

    // Returns all nodes that have had their dependencies fulfilled
    public Set<Node> getAvailableNodes() {
        return availableNodes;
    }
}
