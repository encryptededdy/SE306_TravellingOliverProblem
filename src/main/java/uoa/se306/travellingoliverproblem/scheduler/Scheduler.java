package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleAStar;

/*
Outlines signature for schedulers to adhere to
 */
public abstract class Scheduler {

    // Best schedule found from all iterations
    Schedule bestSchedule;
    private int amountOfProcessors;
    // Graph of all nodes
    protected Graph graph;

    long branchesConsidered = 0;
    long branchesKilled = 0;
    long branchesKilledDuplication = 0;

    // constructor to initialize the input graph and the amount of processors to use
    Scheduler(Graph graph, int amountOfProcessors){
        this.graph = graph;
        this.amountOfProcessors = amountOfProcessors;
    }

    // Initial call to the recursive function, returns a Schedule object
    // Template method pattern
    public Schedule getBestSchedule() {
        //calculateSchedule(new Schedule(amountOfProcessors, graph.getStartingNodes(), graph.getAllNodes() , graph.getComputationalLoad()));
        calculateSchedule(new ScheduleAStar(amountOfProcessors, graph.getStartingNodes(), graph.getAllNodes() , graph.getComputationalLoad()));

        return bestSchedule;
    }

    public long getBranchesConsidered() {
        return branchesConsidered;
    }

    public long getBranchesKilledDuplication() {
        return branchesKilledDuplication;
    }

    public long getBranchesKilled() {
        return branchesKilled;
    }

    public double proportionKilled() {
        return (double)branchesKilled/(branchesConsidered+branchesKilled);
    }

    // Recursive function, to be implemented by children
    protected abstract void calculateSchedule(Schedule currentSchedule);

}
