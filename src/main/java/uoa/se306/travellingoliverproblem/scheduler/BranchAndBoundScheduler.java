package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashSet;

public class BranchAndBoundScheduler extends Scheduler {

    public BranchAndBoundScheduler(Graph graph) {
        super(graph);
    }

    @Override
    public int calculateSchedule(Schedule currentSchedule) {
        return 0;
    }
}
