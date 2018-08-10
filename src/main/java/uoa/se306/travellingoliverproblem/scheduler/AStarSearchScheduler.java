package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.*;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.CostFunction;

import java.util.*;

/*
Scheduler for the A Star Algorithm
 */
public class AStarSearchScheduler extends Scheduler {

    private Set<MinimalSchedule> existingSchedules = new HashSet<>();
    private PriorityQueue<ScheduleAStar> candidateSchedules = new PriorityQueue<>();

    public AStarSearchScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        if (currentSchedule instanceof ScheduleAStar) {
            solveAStar((ScheduleAStar) currentSchedule);
        } else {
            throw new InvalidScheduleException("currentSchedule is not an instance of ScheduleAStar");
        }
    }


    private void solveAStar(ScheduleAStar currentSchedule) {

        branchesConsidered++;
        existingSchedules.add(new MinimalSchedule(currentSchedule)); // store this schedule as visited
        currentSchedule.getCostFunction();
        candidateSchedules.add(currentSchedule);


        while (true) {

            ScheduleAStar partial = candidateSchedules.poll();
            branchesConsidered++;

            // If the first partial schedule in the priority queue is complete
            // It is an optimal schedule
            if (partial.getAvailableNodes().isEmpty()) {
                bestSchedule = partial;
                break;
            } else {
                // Get all the available nodes in the schedule
                Set<Node> availableNodes = new HashSet<>(partial.getAvailableNodes());
                for (Node node : availableNodes) {
                    ScheduledProcessor[] processors = partial.getProcessors();
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

                    // Then, for each processor we can put this node on, actually put it on.
                    for (int j = 0; j < processors.length; j++) {
                        int startTime = processors[j].getEarliestStartAfter(processorEarliestAvailable[j], node.getCost());
                        ScheduleAStar tempSchedule = new ScheduleAStar(partial); // make a new sub-schedule with the new node
                        tempSchedule.addToSchedule(node, j, startTime);
                        MinimalSchedule m = new MinimalSchedule(tempSchedule);
                        if (!existingSchedules.contains(m)) {
                            existingSchedules.add(m);
                            tempSchedule.getCostFunction();
                            candidateSchedules.add(tempSchedule);
                        } else {
                            branchesKilled++;
                            branchesKilledDuplication++;
                        }
                    }
                }
            }
        }
    }
}

