package uoa.se306.travellingoliverproblem.scheduler.heuristics;

import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

public class CostFunction {
    private Schedule partialSchedule;
    private long currentCost;
    private long totalIdleTime;
    private long dataReadyTime;

    public CostFunction(Schedule partialSchedule){
        this.partialSchedule = partialSchedule;
    }

    public long calculateCost(){
        currentCost= calculateCurrentCost();
        totalIdleTime = calculateTotalIdleTime();
        dataReadyTime = calculateDataReadyTime();
        return Math.max(currentCost, Math.max(totalIdleTime, dataReadyTime));
    }

    public long calculateCurrentCost(){
        partialSchedule.getOverallTime();
        return currentCost;
    }

    public long calculateTotalIdleTime(){
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // get idle time of each processor
        }
        return totalIdleTime;
    }

    public long calculateDataReadyTime(){
        ScheduledProcessor[] processors = partialSchedule.getProcessors();
        for(ScheduledProcessor p : processors){
            // get the last end time
        }
        return dataReadyTime;
    }

    public long getBottomLevel(ScheduleEntry entry){

        return 0;
    }

}
