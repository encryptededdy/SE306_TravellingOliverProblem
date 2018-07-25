package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

public class BFSScheduler extends Scheduler {

    public BFSScheduler(Graph graph, int amountOfProcessors){
        super(graph, amountOfProcessors);

    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {

    }
}
