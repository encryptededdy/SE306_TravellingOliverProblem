package uoa.se306.travellingoliverproblem.scheduler;

import gnu.trove.set.hash.THashSet;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.MinimalSchedule;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.graph.NodeComparator;
import uoa.se306.travellingoliverproblem.graph.NodeCostComparator;

import java.util.*;

public class DFSScheduler extends Scheduler {

    // useEquivalentScheduleCulling always enabled.
    private boolean useExistingScheduleCleaner = true;
    private boolean localDuplicateDetectionOnly = false;
    private Set<MinimalSchedule> existingSchedules = new THashSet<>();
    private long startTime;

    private static final int MAX_MEMORY = 20000000;

    public DFSScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors, true);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        startTime = System.currentTimeMillis();
        if (currentSchedule.getUnAddedNodes().stream().allMatch(Node::isIndependent)) {
            //TODO: This only get called when all the nodes are independent at the start, maybe change it to check every partial schedule ?
            doIndependent(currentSchedule);
        } else {
            calculateScheduleRecursive(currentSchedule);
        }
    }

    private void cleanExistingSchedules() {
        long startTime = System.nanoTime();
        int previousSize = existingSchedules.size();
        existingSchedules.removeIf(minimalSchedule -> minimalSchedule.getCost() >= bestSchedule.getCost());
        int cleaned = previousSize - existingSchedules.size();
        long endTime = System.nanoTime();
        System.out.println("Cleaning Took " + (endTime - startTime) / 1000000 + " ms, cleaned " + cleaned + " entries (" + (cleaned * 100 / previousSize) + "%)");
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        calculateScheduleRecursive(currentSchedule, null);
    }

    private void calculateScheduleRecursive(Schedule currentSchedule, PriorityQueue<Node> previousFixedNodes) {
        branchesConsidered++;
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getCost() > currentSchedule.getCost()) {
                System.out.println("Found new best schedule: " + currentSchedule.getOverallTime());
                bestSchedule = currentSchedule;
                // Only run cleaner if it's been at least 5 seconds since we started, otherwise there's no point
                if (!localDuplicateDetectionOnly && useExistingScheduleCleaner && System.currentTimeMillis() > startTime + 5000)
                    cleanExistingSchedules();
            }
            return;
        }

        PriorityQueue<Schedule> candidateSchedules = new PriorityQueue<>();

        NodeComparator comparator = new NodeComparator(currentSchedule.getProcessors());
        PriorityQueue<Node> nodesList  = new PriorityQueue<>(comparator);
        nodesList.addAll(currentSchedule.getAvailableNodes());

        boolean useFixedOrder = false;

        if (previousFixedNodes != null && previousFixedNodes.containsAll(nodesList)) {
            useFixedOrder = true;
        } else if (fixingOrder(currentSchedule)) {
            if (comparator.isOutEdgeCostConsistent()) {
                useFixedOrder = true;
            }
        }

        if (useFixedOrder) {
            Node topNode = nodesList.poll();
            ScheduledProcessor[] processors = currentSchedule.getProcessors();
            // TODO: Don't recalculate for other processors
            int[] processorEarliestAvailable = findProcessorEarliestAvailable(processors, topNode);

            for (int j = 0; j < processors.length; j++) {
                int startTime = processors[j].getEarliestStartAfter(processorEarliestAvailable[j], topNode.getCost());
                Schedule tempSchedule = new Schedule(currentSchedule);
                tempSchedule.addToSchedule(topNode, j, startTime);
                // Only continue if sub-schedule time is under upper bound
                // i.e. skip this branch if its overall time is already longer than the currently known best overall time
                if (bestSchedule == null || tempSchedule.getCost() < bestSchedule.getCost()) {
                    candidateSchedules.add(tempSchedule);
                } else {
                    // drop this branch, because this partial schedule is guaranteed to be worse than what we currently have, based on overallTime
                    branchesKilled++;
                }
            }

            while (!candidateSchedules.isEmpty()) {
                Schedule candidate = candidateSchedules.poll();
                if (bestSchedule == null || candidate.getCost() < bestSchedule.getCost()) {
                    // Only continue if this schedule hasn't been considered before
                    MinimalSchedule minimal = new MinimalSchedule(candidate);
                    if (!localDuplicateDetectionOnly && !existingSchedules.contains(minimal)) {
                        if (existingSchedules.size() < 25000000) existingSchedules.add(minimal);
                        calculateScheduleRecursive(candidate, nodesList);
                    } else {
                        branchesKilled++; // drop this branch
                        branchesKilledDuplication++;
                    }
                } else {
                    branchesKilled += candidateSchedules.size() + 1;
                    break; // It's a priority queue, so we can just drop the rest
                }
            }

        } else { // Else, just do the normal scheduling

            for (Node node : nodesList) {
                // Get the processors in the current schedule
                ScheduledProcessor[] processors = currentSchedule.getProcessors();

                int[] processorEarliestAvailable = findProcessorEarliestAvailable(processors, node);

                //at this point, we have a list of starting times, for each processor, for this node
                //maybe we can order the nodes in terms of their bottomLevel ? (increasing or decreasing ???)
                //if we order them in increasing, we can

                for (int j = 0; j < processors.length; j++) {
                    int startTime = processors[j].getEarliestStartAfter(processorEarliestAvailable[j], node.getCost());
                    Schedule tempSchedule = new Schedule(currentSchedule);
                    tempSchedule.addToSchedule(node, j, startTime);
                    // Only continue if sub-schedule time is under upper bound
                    // i.e. skip this branch if its overall time is already longer than the currently known best overall time
                    if (bestSchedule == null || tempSchedule.getCost() < bestSchedule.getCost()) {
                        candidateSchedules.add(tempSchedule);
                    } else {
                        // drop this branch, because this partial schedule is guaranteed to be worse than what we currently have, based on overallTime
                        branchesKilled++;
                    }
                }
            }
            // used for local duplicate detection
            Set<MinimalSchedule> consideredThisRound = new HashSet<>();

            while (!candidateSchedules.isEmpty()) {
                Schedule candidate = candidateSchedules.poll();
                if (bestSchedule == null || candidate.getCost() < bestSchedule.getCost()) {
                    // Only continue if this schedule hasn't been considered before
                    MinimalSchedule minimal = new MinimalSchedule(candidate);
                    if (localDuplicateDetectionOnly && !consideredThisRound.contains(minimal)) {
                        consideredThisRound.add(minimal);
                        calculateScheduleRecursive(candidate);
                    } else if (!localDuplicateDetectionOnly && !existingSchedules.contains(minimal)) {
                        if (existingSchedules.size() < 25000000) existingSchedules.add(minimal);
                        calculateScheduleRecursive(candidate);
                    } else {
                        branchesKilled++; // drop this branch
                        branchesKilledDuplication++;
                    }
                } else {
                    branchesKilled += candidateSchedules.size() + 1;
                    break; // It's a priority queue, so we can just drop the rest
                }
            }
        }
    }

    //-------------------------------------------------------------
    private void doIndependent(Schedule currentSchedule) {
        branchesConsidered++;
        if (currentSchedule.getUnAddedNodes().isEmpty() && (bestSchedule == null || currentSchedule.getCost() < bestSchedule.getCost())) {
            System.out.println("Found new best schedule: " + currentSchedule.getOverallTime());
            bestSchedule = currentSchedule;
            return;
        }
        ArrayList<Node> nodesList = new ArrayList<>(currentSchedule.getAvailableNodes());
        Node max = Collections.max(nodesList, new NodeCostComparator());
        for (int i = 0; i < currentSchedule.getProcessors().length; i++) {
            Schedule newSchedule = new Schedule(currentSchedule);
            int endtime = currentSchedule.getProcessors()[i].endTime();
            newSchedule.addToSchedule(max, i, endtime);
            if (!existingSchedules.contains(new MinimalSchedule(newSchedule)) && (bestSchedule == null || newSchedule.getCost() < bestSchedule.getCost())) {
                existingSchedules.add(new MinimalSchedule(newSchedule));
                //System.out.println("Calling for node "+max.toString()+" on proc "+i+" "+nodesList.size()+" left");
                doIndependent(newSchedule);
            } else {
                branchesKilled++;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /*
    These methods are used to test if the list of available nodes meet certain conditions
    These conditions must all be true in order for us to "Fix the task order"
     */
    private boolean fixingOrder(Schedule partialSchedule) {
        Set<Node> availableNodes = partialSchedule.getAvailableNodes();
        Node globalChild = null;
        int globalProcessor = 666;
        for (Node node : availableNodes) {
            //if we find a node that has more than 1 parent, or more than 1 child, return false
            if (node.getParents().size() > 1 || node.getChildren().size() > 1) {
                return false;
            }
            //if the node does have a child, check if its the same child as all other nodes that has a child
            if (node.getChildren().size() == 1) {
                for (Node child : node.getChildren().keySet()) {
                    if (globalChild == null) {
                        globalChild = child;
                    } else {
                        // Check if the child node is the same as our child node
                        // I don't know about the equals method in Node, because it also checks its children, which may waste time ?
                        if (!globalChild.equals(child)) {
                            return false;
                        }
                    }
                }
            }

            //if this node has a parent
            //  get the parent node
            //  find which processor its parent is scheduled in
            //  if the processor is not the same as one of the other nodes
            //      return false
            if (node.getParents().size() == 1) {
                for (Node parent : node.getParents().keySet()) {
                    for (int i = 0; i < partialSchedule.getProcessors().length; i++) {
                        if (partialSchedule.getProcessors()[i].contains(parent)) {
                            if (globalProcessor == 666) {
                                globalProcessor = i;
                            } else {
                                if (globalProcessor != i) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private int[] findProcessorEarliestAvailable(ScheduledProcessor[] processors, Node node) {
        // This list stores the starting times(int) for this node in each processor(index)
        int[] processorEarliestAvailable = new int[processors.length];

        // First, calculate the next available time on all nodes, taking into account parents
        for (Node parentNode : node.getParents().keySet()) {
            for (int i = 0; i < processors.length; i++) {
                ScheduleEntry sEntry = processors[i].getEntry(parentNode);
                if (sEntry != null) { // if this parent
                    // Get end time of parent, and the communication cost
                    int endTime = sEntry.getEndTime();
                    int communication = sEntry.getNode().getChildren().get(node);

                    for (int j = 0; j < processors.length; j++) { // for each entry in the processorEarliestAvailable array
                        if (j == i) { // If same proc (no comm cost)
                            if (processorEarliestAvailable[j] < endTime) {
                                processorEarliestAvailable[j] = endTime;
                            }
                        } else { // not same proc (comm cost!)
                            if (processorEarliestAvailable[j] < endTime + communication) {
                                processorEarliestAvailable[j] = endTime + communication;
                            }
                        }
                    }
                    break; // A node cannot be scheduled in multiple processors
                }
            }
        }
        return processorEarliestAvailable;
    }
}

