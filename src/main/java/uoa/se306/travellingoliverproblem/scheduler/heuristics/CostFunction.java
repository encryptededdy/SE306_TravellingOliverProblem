package uoa.se306.travellingoliverproblem.scheduler.heuristics;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashMap;

public class CostFunction {
    private Schedule partialSchedule;
    // we don't need this
    HashMap<Node, Integer> bottomLevelMap;
    // From the partial schedule
    // we can get the node.getBottomLevel()

    private long maxStartTimeAndBottomLevel;
    private float idleTimeAndComputation;
    private long maxDataReadyTimeAndBottomLevel;


    public CostFunction(Schedule partialSchedule , HashMap<Node, Integer> bottomLevelMap){
        this.partialSchedule = partialSchedule;
        this.bottomLevelMap = bottomLevelMap;
    }

    /*
    This method calls all heuristic methods and returns the maximum cost of those methods
     */
    public float calculateCost(){
        maxDataReadyTimeAndBottomLevel();
        maxStartTimeAndBottomLevel();
        idleTimeAndComputation();
        return  Math.max(idleTimeAndComputation, Math.max(maxDataReadyTimeAndBottomLevel, maxStartTimeAndBottomLevel));
    }

    /*
    The first cost function principle is to find the node (already scheduled in a processor)
    That has the maximum (start time + bottom level).
    This method saves this value as maxStartTimeAndBottomLevel.
     */
    private void maxStartTimeAndBottomLevel(){
        maxStartTimeAndBottomLevel = 0;
    }

    /*
    The second cost function principle is to calculate the total idle time of the given schedule
    plus (total computational load / processors)
     */
    private void idleTimeAndComputation(){
        int totalIdelTime = 0;
        int totalComputationTime = 0;
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // for each processor,
        }


    }

    /*
    The third cost function principle is to find the node (available to be scheduled)
    that has the maximum (starting time of node + bottom level)
     */
    private void maxDataReadyTimeAndBottomLevel(){
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // get the last end time
        }
    }


}
