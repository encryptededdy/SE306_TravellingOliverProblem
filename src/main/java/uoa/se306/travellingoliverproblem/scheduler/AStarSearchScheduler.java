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
    protected void calculateSchedule(Schedule currentSchedule){
        if (currentSchedule instanceof ScheduleAStar){
            solveAStar((ScheduleAStar)currentSchedule);
        }else{
            throw new InvalidScheduleException("currentSchedule is not an instance of ScheduleAStar");
        }
    }


    private void solveAStar(ScheduleAStar currentSchedule) {

        branchesConsidered++;
        currentSchedule.getCostFunction();
        candidateSchedules.add(currentSchedule);
        existingSchedules.add(new MinimalSchedule(currentSchedule)); // store this schedule as visited

        while (true) {

            ScheduleAStar partial = candidateSchedules.poll();
            branchesConsidered++;

            // If the first partial schedule in the priority queue is complete
            // It is an optimal schedule
            if (partial.getAvailableNodes().isEmpty()){
                bestSchedule = partial;
                break;
            }else {
                // Get all the available nodes in the schedule
                Set<Node> availableNodes = new HashSet<>(partial.getAvailableNodes());
                for (Node node : availableNodes) {
                    ScheduledProcessor[] processors = partial.getProcessors();

                    for (int i = 0; i < processors.length; i++) {
                        ScheduledProcessor processor = processors[i];
                        int processorStartTime;
                        int startTime = 0;
                        // iterate over all the parent nodes
                        for (Node parentNode : node.getParents().keySet()) {
                            // for all the processors of this current schedule
                            // if any of the processors contains the parentNode
                            // get the endTime of when this parentNode finishes inside that processor
                            for (ScheduledProcessor checkProcessor : partial.getProcessors()) {
                                ScheduleEntry sEntry = checkProcessor.getEntry(parentNode);
                                if (sEntry != null) {
                                    processorStartTime = sEntry.getEndTime();
                                    // add the communication cost between childNode and parentNode if not on same processor
                                    processorStartTime += processor != checkProcessor ? parentNode.getChildren().get(node) : 0;
                                    // if the processorStartTime is larger than the current startTime
                                    // update the current startTime
                                    if (processorStartTime > startTime) {
                                        startTime = processorStartTime;
                                        break;
                                    }
                                }
                            }
                        }
                        //get the earliestStartTime that this available node can be scheduled (In this processor)
                        startTime = processor.getEarliestStartAfter(startTime, node.getCost());
                        //create a copy of our partialSchedule
                        ScheduleAStar tempSchedule = new ScheduleAStar(partial);
                        //add the availableNode into processor i at time startTime in the schedule
                        tempSchedule.addToSchedule(node, i, startTime);
                        MinimalSchedule m = new MinimalSchedule(tempSchedule);

                        if (!existingSchedules.contains(m)){
                            tempSchedule.getCostFunction();
                            existingSchedules.add(m);

                            candidateSchedules.add(tempSchedule);
                        }else{
                            branchesKilled++;
                            branchesKilledDuplication++;
                        }
                    }
                }
            }
        }
    }
}
