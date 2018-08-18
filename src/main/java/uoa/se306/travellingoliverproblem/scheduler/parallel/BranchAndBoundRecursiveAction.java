package uoa.se306.travellingoliverproblem.scheduler.parallel;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicLong;

public class BranchAndBoundRecursiveAction extends RecursiveAction {

    private PriorityQueue<Schedule> schedules;
    public static final int SEQUENTIAL_THRESHOLD = 1; // Magic number????
    private static Schedule bestSchedule;
    public static Graph graph;
    private int amountOfProcessors;

    private static AtomicLong branchesKilledDuplication = new AtomicLong(0);
    private static AtomicLong branchesConsidered = new AtomicLong(0);
    private static AtomicLong branchesKilled = new AtomicLong(0);

    //Initial scheduler task
    public BranchAndBoundRecursiveAction(PriorityQueue<Schedule> schedules, int amountOfProcessors) {
        this.schedules = schedules;
        this.amountOfProcessors = amountOfProcessors;
    }

    @Override
    protected void compute() {
        // Split set into smaller sets to be worked on by threads
        if (schedules.size() > SEQUENTIAL_THRESHOLD) {

            // Split Set
            boolean setDirection = false;
            PriorityQueue<Schedule> firstPartitionedQueue = new PriorityQueue<>();
            PriorityQueue<Schedule> secondPartitionedQueue = new PriorityQueue<>();

            // Split set into 2 equal chunks to pass to subtasks
            while (!schedules.isEmpty()) {
                if (setDirection) {
                    firstPartitionedQueue.add(schedules.poll());
                } else {
                    secondPartitionedQueue.add(schedules.poll());
                }
                setDirection = !setDirection;
            }

            //Fork then join 2 tasks, recursively iterate until set is split fully, and then DFS on inner
            ForkJoinTask.invokeAll(
                    new BranchAndBoundRecursiveAction(firstPartitionedQueue, amountOfProcessors),
                    new BranchAndBoundRecursiveAction(secondPartitionedQueue, amountOfProcessors));

        } else {
            // Actually run the scheduler
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

    //Run a schedule in the DFS scheduler, and either find a completed schedule, or more uncompleted schedules
    private void runDFSScheduler(Schedule currentSchedule) {
        DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors, true);
        scheduler.setBestSchedule(bestSchedule);
        Schedule schedule = scheduler.calculateScheduleParallel(currentSchedule);
        PriorityQueue<Schedule> unfinishedSchedules = scheduler.getUnfinishedSchedules();
        branchesKilled.getAndAdd(scheduler.getBranchesKilled());
        branchesConsidered.getAndAdd(scheduler.getBranchesConsidered());
        branchesKilledDuplication.getAndAdd(scheduler.getBranchesKilledDuplication());

        if (unfinishedSchedules.size() > 0) {
            ForkJoinTask.invokeAll(new BranchAndBoundRecursiveAction(unfinishedSchedules, amountOfProcessors));
            return;
        }
        getAndSetBestSchedule(schedule);
    }

    //Return the best schedule
    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    /*
    Synchronised method to check if the given schedule is better than
    the bestSchedule, if it is set bestSchedule
     */
    private static synchronized void getAndSetBestSchedule(Schedule schedule) {
        if (bestSchedule == null ||  (schedule != null && bestSchedule.getCost() > schedule.getCost())) {
            bestSchedule = schedule;
        }
    }

    // Static method to get branches considered for all instances
    public static long getBranchesConsidered() {
        return branchesConsidered.get();
    }

    // Static method to get branches killed that were duplicate for all instances
    public static long getBranchesKilledDuplication() {
        return branchesKilledDuplication.get();
    }

    // Static method to get branches killed for all instances
    public static long getBranchesKilled() {
        return branchesKilled.get();
    }

    // Static method to get the proportion of branches killed for all instances
    public static double proportionKilled() {
        return (double)branchesKilled.get()/(branchesConsidered.get()+branchesKilled.get());
    }

    // Testing purposes, DO NOT USE
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
