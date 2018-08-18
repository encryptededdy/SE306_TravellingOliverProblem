package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.scheduler.parallel.BranchAndBoundRecursiveAction;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.util.HashSet;
import java.util.Set;

public class ParallelScheduler extends Scheduler {

    public ParallelScheduler(Graph graph, int amountOfProcessors, boolean useDFSCostFunction, boolean isParallelised) {
        super(graph, amountOfProcessors, useDFSCostFunction, isParallelised);
        BranchAndBoundRecursiveAction.graph = graph;
    }

    //Runs the recursive action, to start the parallelisation, gives it 1 empty schedule
    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
//      HybridScheduler initialScheduler = new HybridScheduler(BranchAndBoundRecursiveAction.graph, amountOfProcessors, isParallelised, 1);
//      initialScheduler.getBestSchedule(); //TODO run hybrid if certain size
//      Collection<Schedule> schedules = initialScheduler.getSchedules();
        Set<Schedule> schedules = new HashSet<>();
        schedules.add(currentSchedule);
        BranchAndBoundRecursiveAction bab = new BranchAndBoundRecursiveAction(schedules, amountOfProcessors);
        bab.invoke();
        // Check if an error occurred and that the recursive action completely correctly
        if (bab.isCompletedAbnormally()) {
            System.exit(1); // Bad
        }
        bestSchedule = bab.getBestSchedule();
    }

    @Override
    public long getBranchesConsidered() {
        return BranchAndBoundRecursiveAction.getBranchesConsidered();
    }

    @Override
    public long getBranchesKilledDuplication() {
        return BranchAndBoundRecursiveAction.getBranchesKilledDuplication();
    }

    @Override
    public long getBranchesKilled() {
        return BranchAndBoundRecursiveAction.getBranchesKilled();
    }

    @Override
    public double proportionKilled() {
        return BranchAndBoundRecursiveAction.proportionKilled();
    }
}
