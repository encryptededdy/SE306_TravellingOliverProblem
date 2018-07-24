package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.HashSet;
import java.util.Set;

public class Schedule {
    private Set<Node> addedNodes;
    private Set<Node> unaddedNodes;
    private Set<Node> availableNodes;

    private ScheduledProcessor[] processors;

    public Schedule(int processorCount, Set<Node> availableNodes, Set<Node> allNodes) {
        unaddedNodes = allNodes;
        this.availableNodes = availableNodes;

        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    public ScheduledProcessor[] getProcessors() {
        return processors;
    }

    public boolean addToSchedule(Node node, ScheduledProcessor processor) {
        return false;
    }

    public ScheduledProcessor getEarliestProcessor(Node node) {
        return null;
    }

    private int getEarliestTimeForProcessor(Node node, ScheduledProcessor processor) {
        return 0;
    }

    public Set<Node> getAddedNodes() {
        return addedNodes;
    }

    public Set<Node> getUnaddedNodes() {
        return unaddedNodes;
    }

    public Set<Node> getAvailableNodes() {
        return availableNodes;
    }
}
