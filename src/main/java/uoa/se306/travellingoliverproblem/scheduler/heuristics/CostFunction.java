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


    public CostFunction(Schedule partialSchedule){
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
        int max = Math.max(maxDataReadyTimeAndBottomLevel, maxStartTimeAndBottomLevel);

        if (max > idleTimeAndComputation){
            return idleTimeAndComputation;
        }else{
            return (float)max;
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

        idleTimeAndComputation = ((float)totalIdleTime + (float)partialSchedule.getCOMPUTATIONAL_LOAD()) / (float)processors.length ;;
    }

    /*
    The third cost function principle is to find the node (available to be scheduled)
    that has the maximum (starting time of node + bottom level)
    NOTE: this algorithm only considers available nodes at this moment
     */
    private void maxDataReadyTimeAndBottomLevel(){
        //For each node in the availableNodes set

        for (Node node : availableNodes){
            int largestNodeStartTime = 0;

            for (int i = 0; i < processors.length; i++) {
                ScheduledProcessor processor = processors[i];
                int processorStartTime;
                int startTime = 0;
                // iterate over all the parent nodes
                for (Node parentNode : node.getParents().keySet()) {
                    // for all the processors of this current schedule
                    // if any of the processors contains the parentNode
                    // get the endTime of when this parentNode finishes inside that processor
                    for (ScheduledProcessor checkProcessor : partialSchedule.getProcessors()) {
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

                // record the biggest startTime for this node, in an processor
                if (startTime > largestNodeStartTime){
                    largestNodeStartTime = startTime;
                }
            }
            int tempTempCost= largestNodeStartTime + node.getBottomLevel();
            if (tempTempCost > maxDataReadyTimeAndBottomLevel) {
                maxDataReadyTimeAndBottomLevel = tempTempCost;
            }
        }
    }


}
