package uoa.se306.travellingoliverproblem.parallel;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;

import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class BranchAndBoundRecursiveAction extends RecursiveAction {

    private Collection<Schedule> schedules;
//    private static Logger logger =
//            Logger.getAnonymousLogger();
    // TODO check what optimal threshold is
    private static final int SEQUENTIAL_THRESHOLD = 100;
    private static Schedule bestSchedule;//TODO sync issues
    public static Graph graph;
    private int amountOfProcessors;//TODO Change later
    private boolean useCurrentBestCulling = true; //TODO actually use these
    private boolean useLocalPriorityQueue = true;

    private long branchesConsidered = 0;
    private long branchesKilled = 0;

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
            List<BranchAndBoundRecursiveAction> subTasks = new ArrayList<>();

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

            subTasks.add(new BranchAndBoundRecursiveAction(firstPartitionedQueue, amountOfProcessors));
            subTasks.add(new BranchAndBoundRecursiveAction(secondPartitionedQueue, amountOfProcessors));
            //Fork then join 2 tasks, recursively iterate until set is split fully
            ForkJoinTask.invokeAll(subTasks);

        } else {
            processSchedule(schedules);
        }
    }

    private void processSchedule(Collection<Schedule> schedules) {
//        logger.info("These schedules were processed by "
//                + Thread.currentThread().getName());
        // Iterate through every schedule and work recursively on this
        for (Schedule schedule : schedules) {
            if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
                calculateScheduleRecursive(schedule);
            }
        }
        schedules.clear();
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        List<BranchAndBoundRecursiveAction> subTasks = new ArrayList<>();
        DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors, true);
        scheduler.setBestSchedule(bestSchedule);
        Schedule schedule = scheduler.calculateScheduleParallel(currentSchedule);
        Set<Schedule> unfinishedSchedules = scheduler.getUnfinishedSchedules();
        if (unfinishedSchedules.size() > 0) {
            subTasks.add(new BranchAndBoundRecursiveAction(unfinishedSchedules, amountOfProcessors));
            ForkJoinTask.invokeAll(subTasks);
            return;
        }
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
}
