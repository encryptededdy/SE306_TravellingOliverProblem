package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

public class AStarSearchScheduler extends Scheduler {

    public AStarSearchScheduler(Graph graph) {
        super(graph);
    }

    @Override
    public int calculateSchedule(Schedule currentSchedule) {
        return 0;
    }
}
