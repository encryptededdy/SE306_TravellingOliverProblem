package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
This class describes a section of a schedule for a given input graph
 */
public class Schedule {
    private Set<Node> unAddedNodes = new HashSet<>();
    private Set<Node> availableNodes = new HashSet<>();

    private ScheduledProcessor[] processors;

    // Constructor
    public Schedule(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes) {
        unAddedNodes.addAll(allNodes);
        this.availableNodes.addAll(availableNodes);

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
    public void addToSchedule(Node node, int processorNo, int startTime) {
        processors[processorNo].add(node, startTime);
        unAddedNodes.remove(node);
        availableNodes.remove(node);
        // Check to see if any new nodes become available
        for (Node child : node.getChildren().keySet()) {
            boolean available = true;
            for (Node childParent : child.getParents().keySet()) {
                if (unAddedNodes.contains(childParent)) available = false;
            }
            if (available) availableNodes.add(child);
        }
    }

    // Returns all nodes that have not been added to the schedule
    public Set<Node> getUnAddedNodes() {
        return unAddedNodes;
    }

    // Returns all nodes that have had their dependencies fulfilled
    public Set<Node> getAvailableNodes() {
        return availableNodes;
    }

    public boolean checkValidity() {
        // TODO: Implement Schedule validity check (i.e. no overlaps etc.)
        return true;
    }
}
