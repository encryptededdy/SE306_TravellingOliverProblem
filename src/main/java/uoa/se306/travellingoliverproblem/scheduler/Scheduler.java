package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

/*
Outlines signature for schedulers to adhere to
 */
public abstract class Scheduler {

    // Best schedule found from all iterations
    Schedule bestSchedule;
    int amountOfProcessors;
    // Graph of all nodes
    protected Graph graph;

    public static int COMPUTATIONAL_LOAD;

    boolean isParallelised;
    long branchesConsidered = 0;
    long branchesKilled = 0;
    long branchesKilledDuplication = 0;
    private boolean useDFSCostFunction;

    // constructor to initialize the input graph and the amount of processors to use
    Scheduler(Graph graph, int amountOfProcessors, boolean useDFSCostFunction, boolean isParallelised) {
        this.useDFSCostFunction = useDFSCostFunction;
        this.isParallelised = isParallelised;
        this.graph = graph;
        COMPUTATIONAL_LOAD = graph.getComputationalLoad();
        this.amountOfProcessors = amountOfProcessors;
    }

    public void setBestSchedule(Schedule bestSchedule) {
        this.bestSchedule = bestSchedule;
    }
    // Initial call to the recursive function, returns a Schedule object
    // Template method pattern
    public Schedule getBestSchedule() {
        calculateSchedule(new Schedule(amountOfProcessors, graph.getStartingNodes(), graph.getAllNodes(), useDFSCostFunction));
        bestSchedule.checkValidity();
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
