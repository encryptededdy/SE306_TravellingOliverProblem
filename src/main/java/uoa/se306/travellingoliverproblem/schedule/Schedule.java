package uoa.se306.travellingoliverproblem.schedule;

import gnu.trove.set.hash.THashSet;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.CostFunction;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/*
This class describes a section of a schedule for a given input graph
 */
public class Schedule implements Comparable<Schedule>{
    private Set<Node> unAddedNodes = new THashSet<>();
    private Set<Node> availableNodes = new THashSet<>();
    private float cost = 0;
    private int overallTime = 0;

    public float getCost() {
        return cost;
    }

    private ScheduledProcessor[] processors;

    // Constructor
    public Schedule(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes) {
        unAddedNodes.addAll(allNodes);
        this.availableNodes.addAll(availableNodes); // Keeps track of which nodes becomes available to be added in a processor

        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    // Copy constructor
    public Schedule(Schedule toCopy) {
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

    private void calculateCostFunction(){
        CostFunction costFunction = new CostFunction(this, Scheduler.COMPUTATIONAL_LOAD);
        cost = costFunction.calculateCost();
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
        calculateCostFunction();
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

    public boolean checkValidity() throws InvalidScheduleException {
        // TODO: Implement Schedule validity check (i.e. no overlaps etc.)

        try {
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

                                } else if (processors[j].getEntry(parentNode).getEndTime() + cost > scheduleEntry.getStartTime()){
                                    // Check the parent task is completed + communication time before the child task starts
                                    throw new InvalidScheduleException("Child task started before Parent task completed + communication cost on another processor");
                                }
                            }
                        }
                    });
                });


            }
        } catch (InvalidScheduleException e) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Schedule otherSchedule) {
        //System.out.println("this cost: " + cost + " that cost: "+ otherSchedule.getCost());
        return Float.compare(cost, otherSchedule.cost);
    }

}
