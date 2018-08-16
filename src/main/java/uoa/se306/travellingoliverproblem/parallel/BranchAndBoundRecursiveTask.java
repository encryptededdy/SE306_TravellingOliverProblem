package uoa.se306.travellingoliverproblem.parallel;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.MinimalSchedule;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

public class BranchAndBoundRecursiveTask extends RecursiveTask<Schedule> {

    private PriorityQueue<Schedule> schedules;
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
    public BranchAndBoundRecursiveTask(PriorityQueue<Schedule> schedules, int amountOfProcessors) {
        this.schedules = schedules;
        this.amountOfProcessors = amountOfProcessors;
    }

    @Override
    protected Schedule compute() {
        // Split set into smaller sets to be worked on by threads
        if (schedules.size() > SEQUENTIAL_THRESHOLD) {
            List<BranchAndBoundRecursiveTask> subTasks = new ArrayList<>();

            // Split Set
            //TODO change this as inefficient, and may need better load balancing
            boolean setDirection = false;
            PriorityQueue<Schedule> firstPartitionedQueue = new PriorityQueue<>();
            PriorityQueue<Schedule> secondPartitionedQueue = new PriorityQueue<>();

            // Shitty load balancing, fix TODO
            while (schedules.size() > 0) {
                if (setDirection) {
                    firstPartitionedQueue.add(schedules.poll());
                } else {
                    secondPartitionedQueue.add(schedules.poll());
                }
                setDirection = !setDirection;
            }

            subTasks.add(new BranchAndBoundRecursiveTask(firstPartitionedQueue, amountOfProcessors));
            subTasks.add(new BranchAndBoundRecursiveTask(secondPartitionedQueue, amountOfProcessors));
            //Fork then join 2 tasks, recursively iterate until set is split fully
            ForkJoinTask.invokeAll(subTasks)
                    .stream()
                    .map(ForkJoinTask::join)
                    .forEach(BranchAndBoundRecursiveTask::getAndSetBestSchedule);

        } else {
            processSchedule(schedules);
        }
        return bestSchedule;
    }

    private void processSchedule(PriorityQueue<Schedule> schedules) {
//        logger.info("These schedules were processed by "
//                + Thread.currentThread().getName());
        // Iterate through every schedule and work recursively on this
        Schedule schedule;
        while(schedules.size() > 0) {
            schedule = schedules.poll();
            if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
                calculateScheduleRecursive(schedule);
            }
        }
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors, true);
        scheduler.setBestSchedule(bestSchedule);
        scheduler.calculateSchedule(currentSchedule);
        Schedule schedule = scheduler.getBestSchedule();
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
