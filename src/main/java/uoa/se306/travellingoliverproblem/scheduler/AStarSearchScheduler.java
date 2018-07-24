package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

/*
Scheduler for the A Star Algorithm
 */
public class AStarSearchScheduler extends Scheduler {

    public AStarSearchScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    public void calculateSchedule(Schedule currentSchedule) {
    }
}
