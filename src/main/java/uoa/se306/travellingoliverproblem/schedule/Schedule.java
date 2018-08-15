package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;

import java.util.*;
import java.util.stream.Collectors;

/*
This class describes a section of a schedule for a given input graph
 */
public class Schedule implements Comparable<Schedule> {
    private Set<Node> unAddedNodes = new HashSet<>();
    private Set<Node> availableNodes = new HashSet<>();
    private boolean useDFSCostFunction;
    private float cost = 0;
    private int overallTime = 0;
    private int maxStartTimeBottomLevel = 0;
    private int idleTime = 0;

    public float getCost() {
        return cost;
    }

    private ScheduledProcessor[] processors;

    // Constructor
    public Schedule(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes, boolean useDFSCostFunction) {
        this.useDFSCostFunction = useDFSCostFunction;
        unAddedNodes.addAll(allNodes);
        this.availableNodes.addAll(availableNodes); // Keeps track of which nodes becomes available to be added in a processor

        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    // Copy constructor
    public Schedule(Schedule toCopy) {
        idleTime = toCopy.idleTime;
        maxStartTimeBottomLevel = toCopy.maxStartTimeBottomLevel;
        useDFSCostFunction = toCopy.useDFSCostFunction;
        processors = new ScheduledProcessor[toCopy.processors.length];
        cost = toCopy.cost;
        overallTime = toCopy.overallTime;
        unAddedNodes = new HashSet<>(toCopy.unAddedNodes);
        availableNodes = new HashSet<>(toCopy.availableNodes);
        // Copy the ScheduledProcessors within using copy constructor
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new ScheduledProcessor(toCopy.processors[i]);
        }
    }

    private void calculateCostFunction(Node node, int startTime, float idleTimeAndComputation) {
        maxStartTimeAndBottomLevel(node, startTime);
        if (useDFSCostFunction) {
            cost = Math.max(maxStartTimeBottomLevel, idleTimeAndComputation);
        } else {
            // Use the third part of the cost function, only if we're not doing DFS
            int maxDataReadyTimeAndBottomLevel = maxDataReadyTimeAndBottomLevel();
            int max = Math.max(maxDataReadyTimeAndBottomLevel, maxStartTimeBottomLevel);
            if (max > idleTimeAndComputation) {
                cost = (float) max;
            } else {
                cost = idleTimeAndComputation;
            }
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
    public String toString() {
        return Arrays.stream(processors).map(ScheduledProcessor::toString).sorted().collect(Collectors.joining());
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

    public MinimalSchedule testAddToSchedule(Node node, int processorNo, int startTime) {
        ScheduledProcessor[] processorsCopy = processors.clone();
        processorsCopy[processorNo] = new ScheduledProcessor(processorsCopy[processorNo]); // copy appropriate proc
        processorsCopy[processorNo].add(node, startTime);
        return new MinimalSchedule(processorsCopy);
    }

    // Adds a node to a given processor
    public void addToSchedule(Node node, int processorNo, int startTime) {
        // We need to calculate idleTime BEFORE we add the new node, as it needs to know what the previous state was
        float idleTimeAndComputation = idleTimeAndComputation(node, processorNo, startTime);
        // Add the new node...
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
        calculateCostFunction(node, startTime, idleTimeAndComputation);
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

    /*
    The first cost function principle is to find the node (already scheduled in a processor)
    That has the maximum (start time + bottom level).
    This method saves this value as maxStartTimeAndBottomLevel.
     */
    private void maxStartTimeAndBottomLevel(Node node, int startTime) {
        // done iteratively
        int tempCost = startTime + node.getBottomLevel();
        if (tempCost > maxStartTimeBottomLevel) maxStartTimeBottomLevel = tempCost;
    }

    /*
    The second cost function principle is to calculate the
    (total idle time + total computational load ) / processors
    Run BEFORE adding the new node
     */
    private float idleTimeAndComputation(Node node, int processorNo, int startTime) {
        int endTime = processors[processorNo].endTime();
        if (startTime > endTime) {
            idleTime += (startTime - endTime); // if we start after the last node, with a gap
        } else if (startTime < endTime) {
            idleTime -= node.getCost(); // if we start before last node
        } // if we start on the last node; no change to idle time
        return ((float) idleTime + (float) Scheduler.COMPUTATIONAL_LOAD) / (float) processors.length;
    }

    /*
    The third cost function principle is to find the node (available to be scheduled)
    that has the maximum (minimal starting time of node + bottom level)
    NOTE: this algorithm only considers available nodes at this moment
     */
    private int maxDataReadyTimeAndBottomLevel() {
        int output = 0;
        // For each node in the availableNodes set
        for (Node node : availableNodes) {
            int[] processorEarliestAvailable = new int[processors.length];
            // First, calculate the next available time on all nodes, taking into account parents
            for (Node parentNode : node.getParents().keySet()) {
                for (int i = 0; i < processors.length; i++) {
                    ScheduleEntry sEntry = processors[i].getEntry(parentNode);
                    //if there is a parent node in this processor
                    if (sEntry != null) {
                        // Get end time of parent, and the communication cost
                        int endTime = sEntry.getEndTime();
                        int communication = sEntry.getNode().getChildren().get(node);
                        // for each entry in the processorEarliestAvailable array
                        for (int j = 0; j < processors.length; j++) {
                            if (j == i) { // If same processor (no communication cost)
                                if (processorEarliestAvailable[j] < endTime) {
                                    processorEarliestAvailable[j] = endTime;
                                }
                            } else { // not same processor (communication cost!)
                                if (processorEarliestAvailable[j] < endTime + communication) {
                                    processorEarliestAvailable[j] = endTime + communication;
                                }
                            }
                        }
                        break; // A node cannot be scheduled in multiple processors
                    }
                }
            }

            // we now have an array, index represents which processor, element represents earliestStartTime
            int smallest = 0;
            for (int j = 0; j < processors.length; j++) {
                int temp = processorEarliestAvailable[j];
                // get the earliestStartTime that this available node can be scheduled (In this processor)
                int startTime = processors[j].getEarliestStartAfter(temp, node.getCost());
                if (startTime < smallest) {
                    smallest = startTime;
                }
            }

            int tempCost = smallest + node.getBottomLevel();
            if (tempCost > output) {
                output = tempCost;
            }
        }
        return output;
    }

    public void setMaxOverallTime() {
        overallTime = Integer.MAX_VALUE;
    }

    public void checkValidity() throws InvalidScheduleException {
        // Check that there are no unadded nodes in the schedule.
        if (unAddedNodes.size() > 0) {
            throw new InvalidScheduleException("There are " + unAddedNodes.size() + " nodes that have not" +
                    " been added to a schedule");
        }
        /*
        Check that for each Schedule Entry in each processor, each parent has a schedule entry where the EndTime
        is before the start time of the original schedule entry if it is on the same processor
        or if it is on a different processor, add the communication cost to the parents end time.
        */
        for (int i = 0; i < processors.length; i++) {
            ScheduledProcessor processor = processors[i];
            int processorIndex = i; // For use in lambda expression

            ArrayList<ScheduleEntry> scheduleEntries = new ArrayList<>();
            processor.getFullSchedule().forEach(scheduleEntry -> {
                // put all the schedule entries in an arraylist so the can be sorted
                scheduleEntries.add(scheduleEntry);
            });
            Collections.sort(scheduleEntries);

            int prevNodeFinishTime = 0;
            scheduleEntries.forEach(scheduleEntry -> {
                // Check for no overlaps on the same processor
                if (scheduleEntry.getStartTime() < prevNodeFinishTime) {
                    throw new InvalidScheduleException("Invalid Schedule Exception: Schedule Entry Overlap");
                }

                // For each of the parents of the schedule entry
                scheduleEntry.getNode().getParents().forEach((parentNode, cost) -> {
                    for (int j = 0; j < processors.length; j++) {
                        if (processors[j].contains(parentNode)) { // TODO: Change this to not use contains, and instead cache the output (faster)

                            // If the parent is scheduled on the same processor
                            if (j == processorIndex) {
                                if (processors[j].getEntry(parentNode).getEndTime() > scheduleEntry.getStartTime()) {
                                    // Check the parent task is completed before the child task starts
                                    throw new InvalidScheduleException("Child task started before Parent task completed on the same processor");
                                }

                            } else if (processors[j].getEntry(parentNode).getEndTime() + cost > scheduleEntry.getStartTime()) {
                                // Check the parent task is completed + communication time before the child task starts
                                throw new InvalidScheduleException("Child task started before Parent task completed + communication cost on another processor");
                            }
                        }
                    }
                });
            });


        }
    }

    @Override
    public int compareTo(Schedule otherSchedule) {
        //System.out.println("this cost: " + cost + " that cost: "+ otherSchedule.getCost());
        return Float.compare(cost, otherSchedule.cost);
    }

}
