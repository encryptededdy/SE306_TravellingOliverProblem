package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

/*
Scheduler for the BranchAndBound Algorithm
 */
public class BranchAndBoundScheduler extends Scheduler {

    public BranchAndBoundScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
    }
}
