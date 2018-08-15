package uoa.se306.travellingoliverproblem.scheduler;

import gnu.trove.set.hash.THashSet;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.*;

import java.util.*;

public class DFSScheduler extends Scheduler {

    // useEquivalentScheduleCulling always enabled.
    private boolean useExistingScheduleCleaner = true;
    private boolean localDuplicateDetectionOnly = false;
    private Set<MinimalSchedule> existingSchedules = new THashSet<>();
    private long startTime;

    public DFSScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors, true);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        startTime = System.currentTimeMillis();
        calculateScheduleRecursive(currentSchedule);
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

    private void calculateScheduleRecursive(Schedule currentSchedule, ArrayList<Node> previousFixedNodes) {
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

        //TODO: try use a priority queue
        ArrayList<Node> nodesList = new ArrayList<>(currentSchedule.getAvailableNodes());
        // Check if the available nodes set can be fixed in fork-join ordering
        // if it can, order it, and then verify the list

        //TODO: reduce this massive overhead
        boolean useFixedOrder = false;
        if (previousFixedNodes != null && nodesList.containsAll(previousFixedNodes)) {
            useFixedOrder = true;
        } else if (fixingOrder(currentSchedule)) {
            nodesList = taskSorting(nodesList, currentSchedule.getProcessors());
            if (verifyOutEdgeOrder(nodesList)) {
                useFixedOrder = true;
            }
        }

        if (useFixedOrder) {
            Node topNode = nodesList.get(0);
            ScheduledProcessor[] processors = currentSchedule.getProcessors();
            // TODO: Don't recalculate for other processors
            int[] processorEarliestAvailable = findProcesserEarliestAvaliable(processors, topNode);

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
        } else { // Else, just do the normal scheduling

            for (Node node : nodesList) {
                // Get the processors in the current schedule
                ScheduledProcessor[] processors = currentSchedule.getProcessors();

                int[] processorEarliestAvailable = findProcesserEarliestAvaliable(processors, node);

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

    private int[] findProcesserEarliestAvaliable(ScheduledProcessor[] processors, Node node) {
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

    /*
    Only call this method if the fixingOrder() method returns true
    This method sorts the available nodes in INCREASING data-ready time (drt)
    The drt of the free node is the finishing time of its parents + communication cost (We always take into account the communication cost).
    If the node has no parent, set drt to 0
    If nodes have same Data-Ready-Time, then sort them in DECREASING out edge cost
    If the node have no parent, set out edge cost to 0 (which means

    verify that the available nodes are in decreasing out-edge order
     */
    private ArrayList<Node> taskSorting(ArrayList<Node> theList, ScheduledProcessor[] processors) {
        Collections.sort(theList, new NodeComparator(processors));

        //System.out.println("the list looks like this after sorting" + theList);
        return theList;
    }

    /*
    This method takes a sorted ArrayList<Node> and checks if this list is sorted in
    non-increasing out-edge costs of the Node.
     */
    private boolean verifyOutEdgeOrder(ArrayList<Node> theList) {
        //get the first node in the list
        //for every other node in the list
        //  if first node out-edge cost is larger than or equal to the second element
        //      first = second
        //  else
        //      return false;
        //return true;
        if (theList.size() > 1) {
            Node firstNode = theList.get(0);
            for (int i = 1; i < theList.size() - 1; i++) {
                Node secondNode = theList.get(i);
                int outEdgeCostFirstNode = (firstNode.getChildren().isEmpty()) ? 0 : firstNode.getChildren().values().iterator().next();
                int outEdgeCostSecondNode = (secondNode.getChildren().isEmpty()) ? 0 : secondNode.getChildren().values().iterator().next();
                if (outEdgeCostFirstNode >= outEdgeCostSecondNode) {
                    firstNode = secondNode;
                } else {
                    return false;
                }
            }
        }
        return true;

    }
}

