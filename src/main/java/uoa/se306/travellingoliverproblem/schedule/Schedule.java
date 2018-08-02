package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/*
This class describes a section of a schedule for a given input graph
 */
public class Schedule {
    private Set<Node> unAddedNodes = new HashSet<>();
    private Set<Node> availableNodes = new HashSet<>();
    private int overallTime = 0;

    private ScheduledProcessor[] processors;

    // Constructor
    public Schedule(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes) {
        unAddedNodes.addAll(allNodes); // Why is this adding all the nodes to the unAdded set ?
                                        //because initially, none of the nodes have been added the schedule
        this.availableNodes.addAll(availableNodes); // Keeps track of which nodes becomes available to be added in a processor

        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    // Copy constructor
    public Schedule(Schedule toCopy) {
        processors = new ScheduledProcessor[toCopy.processors.length];
        overallTime = toCopy.overallTime;
        unAddedNodes = new HashSet<>(toCopy.unAddedNodes);
        availableNodes = new HashSet<>(toCopy.availableNodes);
        // Copy the ScheduledProcessors within using copy constructor
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new ScheduledProcessor(toCopy.processors[i]);
        }
    }

    // Returns all the processors
    public ScheduledProcessor[] getProcessors() {
        return processors;
    }

    // Return a set containing the set of the string
    private Set<String> processorStringSet() {
        return Arrays.stream(processors).map(ScheduledProcessor::toString).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        String stringRep = Arrays.stream(processors).map(ScheduledProcessor::toString).sorted().collect(Collectors.joining());
        return stringRep.hashCode();
    }

    // Compare equality of Schedule, including mirrored schedules
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Schedule) {
            return processorStringSet().equals(((Schedule) obj).processorStringSet());
        }
        return false;
    }

    // Adds a node to a given processor
    public void addToSchedule(Node node, int processorNo, int startTime) {
        processors[processorNo].add(node, startTime);
        if (node.getCost() + startTime > overallTime) {
            overallTime = node.getCost() + startTime;
        }
        unAddedNodes.remove(node);
        availableNodes.remove(node);
        // Check to see if any new nodes become available
        for (Node child : node.getChildren().keySet()) {
            boolean available = true;
            // check if the dependencies of this child has been fulfilled (added into processor)
            for (Node childParent : child.getParents().keySet()) {
                if (unAddedNodes.contains(childParent)) available = false;
            }
            // If the child has had all its dependencies fulfilled, add the child to the available set
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

    public int getOverallTime() {
        return overallTime;
    }

    public void setMaxOverallTime(){
        overallTime = Integer.MAX_VALUE;
    }

    public boolean checkValidity() {
        // TODO: Implement Schedule validity check (i.e. no overlaps etc.)
        return true;
    }
}
