package uoa.se306.travellingoliverproblem.scheduler.heuristics;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.Set;

public class CostFunction {
    private Schedule partialSchedule;
    private ScheduledProcessor[] processors;
    private Set<Node> availableNodes;


    private int maxStartTimeAndBottomLevel;
    private float idleTimeAndComputation;
    private int maxDataReadyTimeAndBottomLevel;

    private int computationalLoad;


    public CostFunction(Schedule partialSchedule, int computationalLoad){
        this.computationalLoad = computationalLoad;
        this.partialSchedule = partialSchedule;
        maxStartTimeAndBottomLevel = 0;
        idleTimeAndComputation = 0.0f;
        maxDataReadyTimeAndBottomLevel =0;
        processors = partialSchedule.getProcessors();
        availableNodes = partialSchedule.getAvailableNodes();
    }

    /*
    This method calls all heuristic methods and returns the maximum cost of those methods
     */
    public float calculateCost(){
        maxStartTimeAndBottomLevel();
        idleTimeAndComputation();
        maxDataReadyTimeAndBottomLevel();
        //System.out.println("startTime: "+ maxStartTimeAndBottomLevel + "-----idle: "+ idleTimeAndComputation + "-----dataready: "+ maxDataReadyTimeAndBottomLevel);
        int max = Math.max(maxDataReadyTimeAndBottomLevel, maxStartTimeAndBottomLevel);

        if (max > idleTimeAndComputation){
            return (float)max;
        } else {
            return idleTimeAndComputation;
        }

    }

    /*
    The first cost function principle is to find the node (already scheduled in a processor)
    That has the maximum (start time + bottom level).
    This method saves this value as maxStartTimeAndBottomLevel.
     */
    private void maxStartTimeAndBottomLevel(){
        //For processors in partialSchedule
        //  For each Node in the processor
        //      get its startTime
        //      get its bottomLevel
        //      Add startTime + bottomLevel
        //      if startTime + bottomLevel > maxStartTimeAndBottomLevel
        //          maxStartTimeAndBottomLevel = startTime + bottomLevel
        int tempCost;
        for (ScheduledProcessor pro : processors ){
            for (ScheduleEntry node : pro.getFullSchedule()){
                tempCost = node.getStartTime() + node.getNode().getBottomLevel();
                if (tempCost > maxStartTimeAndBottomLevel){
                    maxStartTimeAndBottomLevel = tempCost;
                }
            }
        }

    }

    /*
    The second cost function principle is to calculate the
    (total idle time + total computational load ) / processors
     */
    private void idleTimeAndComputation(){
        int totalIdleTime = 0;
        int currentTimeCounter = 0;

        //For each processor p
        //  For each scheduleEntry node in p
        //      if (node start time is LARGER than currentTimeCounter) {
        //          get idle time: is between currentTimeCounter and startTime of this node
        //          update the currentTimeCounter to be the nodes endTime
        //          add the nodes (execution time, weight, cost, length) to the totalComputationalTime
        //      else
        //          update the currentTimeCounter to be the nodes endTime
        //          add the nodes (execution time, weight, cost, length) to the totalComputationalTime
        //idleTimeAndComputation = totalIdleTime + (totalComputationalTime / processors.size())

        for(ScheduledProcessor p : processors){
            for (ScheduleEntry node : p.getFullSchedule()){
                if (node.getStartTime() > currentTimeCounter){
                    totalIdleTime = totalIdleTime + node.getStartTime() - currentTimeCounter;
                    currentTimeCounter = node.getEndTime();
                }else{
                    currentTimeCounter = node.getEndTime();
                }
            }
        }

        idleTimeAndComputation = (float)totalIdleTime + (float)computationalLoad / (float)processors.length;
    }

    /*
    The third cost function principle is to find the node (available to be scheduled)
    that has the maximum (minimal starting time of node + bottom level)
    NOTE: this algorithm only considers available nodes at this moment
     */
    private void maxDataReadyTimeAndBottomLevel(){
        //For each node in the availableNodes set

        for (Node node: availableNodes) {
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
            //we not have an array, index represents which processor, element represents earliestStartTime
            int smallest = 0;
            for (int j = 0; j < processors.length; j++) {
                int temp = processorEarliestAvailable[j];
                //get the earliestStartTime that this available node can be scheduled (In this processor)
                int startTime = processors[j].getEarliestStartAfter(temp , node.getCost());
                if (startTime < smallest){
                    smallest = startTime;
                }
                //System.out.println("temp is: "+ temp +" startTime is: "+ startTime);
            }

            int tempCost = smallest + node.getBottomLevel();
            if (tempCost > maxDataReadyTimeAndBottomLevel){
                maxDataReadyTimeAndBottomLevel = tempCost;
            }
        }
    }
}
