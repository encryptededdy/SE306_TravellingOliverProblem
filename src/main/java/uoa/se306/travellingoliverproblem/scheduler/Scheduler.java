package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

/*
Outlines signature for schedulers to adhere to
 */
public abstract class Scheduler {

    // Best schedule found from all iterations
    private Schedule bestSchedule;
    private int amountOfProcessors;
    // Graph of all nodes
    private Graph graph;

    public Scheduler(Graph graph, int amountOfProcessors){
        this.graph = graph;
        this.amountOfProcessors = amountOfProcessors;
    }

    // Initial call to the recursive function
    public Schedule getBestSchedule() {
        calculateSchedule(new Schedule(amountOfProcessors, graph.getStartingNodes(), graph.getAllNodes()));
        return bestSchedule;
    }

    // Recursive function, to be implemented by children
    public abstract void calculateSchedule(Schedule currentSchedule);
}
