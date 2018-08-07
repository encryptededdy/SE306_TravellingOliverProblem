package uoa.se306.travellingoliverproblem.scheduler.heuristics;

import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

public class CostFunction {
    private Schedule partialSchedule;
    private long maxStartTimeAndBottomLevel;
    private float idleTimeAndComputation;
    private long maxDataReadyTimeAndBottomLevel;


    public CostFunction(Schedule partialSchedule){
        this.partialSchedule = partialSchedule;
    }

    public float calculateCost(){
        maxDataReadyTimeAndBottomLevel();
        maxStartTimeAndBottomLevel();
        idleTimeAndComputation();
        return  Math.max(maxDataReadyTimeAndBottomLevel, Math.max(idleTimeAndComputation, maxStartTimeAndBottomLevel));
    }

    public void maxStartTimeAndBottomLevel(){
        maxStartTimeAndBottomLevel = 0;
    }

    public void idleTimeAndComputation(){
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // get idle time of each processor
        }
    }

    public void maxDataReadyTimeAndBottomLevel(){
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // get the last end time
        }
    }


}
