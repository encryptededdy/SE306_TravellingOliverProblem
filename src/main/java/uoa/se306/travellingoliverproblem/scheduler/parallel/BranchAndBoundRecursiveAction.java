package uoa.se306.travellingoliverproblem.scheduler.parallel;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;

import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicLong;

public class BranchAndBoundRecursiveAction extends RecursiveAction {

    private Collection<Schedule> schedules;
    // TODO check what optimal threshold is
    public static final int SEQUENTIAL_THRESHOLD = 4; // Magic number????
    private static Schedule bestSchedule;
    public static Graph graph;
    private int amountOfProcessors;

    private static AtomicLong branchesKilledDuplication = new AtomicLong(0);
    private static AtomicLong branchesConsidered = new AtomicLong(0);
    private static AtomicLong branchesKilled = new AtomicLong(0);

    //TODO research creating a queue

    //Initial scheduler task
    public BranchAndBoundRecursiveAction(Collection<Schedule> schedules, int amountOfProcessors) {
        this.schedules = schedules;
        this.amountOfProcessors = amountOfProcessors;
    }

    @Override
    protected void compute() {
        // Split set into smaller sets to be worked on by threads
        if (schedules.size() > SEQUENTIAL_THRESHOLD) {

            // Split Set
            //TODO change this as inefficient, and may need better load balancing
            boolean setDirection = false;
            PriorityQueue<Schedule> firstPartitionedQueue = new PriorityQueue<>();
            PriorityQueue<Schedule> secondPartitionedQueue = new PriorityQueue<>();

            // Shitty load balancing, fix TODO
            for (Schedule schedule : schedules) {
                if (setDirection) {
                    firstPartitionedQueue.add(schedule);
                } else {
                    secondPartitionedQueue.add(schedule);
                }
                setDirection = !setDirection;
            }
            schedules.clear();

            //Fork then join 2 tasks, recursively iterate until set is split fully
            ForkJoinTask.invokeAll(
                    new BranchAndBoundRecursiveAction(firstPartitionedQueue, amountOfProcessors),
                    new BranchAndBoundRecursiveAction(secondPartitionedQueue, amountOfProcessors));

        } else {
            processSchedule(schedules);
        }
    }

    // Process a collection of schedules
    private void processSchedule(Collection<Schedule> schedules) {
        // Iterate through every schedule and work recursively on this
        for (Schedule schedule : schedules) {
            if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
                runDFSScheduler(schedule);
            }
        }
        schedules.clear();
    }

    private void runDFSScheduler(Schedule currentSchedule) {
        DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors, true);
        scheduler.setBestSchedule(bestSchedule);
        Schedule schedule = scheduler.calculateScheduleParallel(currentSchedule);
        Set<Schedule> unfinishedSchedules = scheduler.getUnfinishedSchedules();
        if (unfinishedSchedules.size() > 0) {
            ForkJoinTask.invokeAll(new BranchAndBoundRecursiveAction(unfinishedSchedules, amountOfProcessors));
            return;
        }
        branchesKilled.getAndAdd(scheduler.getBranchesKilled());
        branchesConsidered.getAndAdd(scheduler.getBranchesConsidered());
        branchesKilledDuplication.getAndAdd(scheduler.getBranchesKilledDuplication());
        getAndSetBestSchedule(schedule);
    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }
    //Synchronised method to set bestSchedule
    private static synchronized void getAndSetBestSchedule(Schedule schedule) {
        if (bestSchedule == null || bestSchedule.getCost() > schedule.getCost()) {
            bestSchedule = schedule;
        }
    }

    public static long getBranchesConsidered() {
        return branchesConsidered.get();
    }

    public static long getBranchesKilledDuplication() {
        return branchesKilledDuplication.get();
    }

    public static long getBranchesKilled() {
        return branchesKilled.get();
    }

    public static double proportionKilled() {
        return (double)branchesKilled.get()/(branchesConsidered.get()+branchesKilled.get());
    }

    // Testing purposes
    @Deprecated
    public static void reset() {
        branchesKilled = new AtomicLong(0);
        branchesKilledDuplication = new AtomicLong(0);
        branchesConsidered = new AtomicLong(0);
        bestSchedule = null;
        graph = null;
        DFSScheduler.reset();
    }
}
