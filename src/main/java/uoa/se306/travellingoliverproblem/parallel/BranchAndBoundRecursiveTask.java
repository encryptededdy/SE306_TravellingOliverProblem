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
    private static Logger logger =
            Logger.getAnonymousLogger();
    // TODO check what optimal threshold is
    private static final int SEQUENTIAL_THRESHOLD = 200;
    private static Schedule bestSchedule;//TODO sync issues
    public static Graph graph;
    private int amountOfProcessors = 2;//TODO Change later
    private boolean useCurrentBestCulling = true; //TODO actually use these
    private boolean useLocalPriorityQueue = true;

    private long branchesConsidered = 0;
    private long branchesKilled = 0;

    //TODO research creating a queue

    //Initial scheduler task
    public BranchAndBoundRecursiveTask(PriorityQueue<Schedule> schedules) {
        this.schedules = schedules;
    }

    //Following scheduler tasks
    public BranchAndBoundRecursiveTask(PriorityQueue<Schedule> schedules, Schedule bestSchedule) {
        this.bestSchedule = bestSchedule;
        this.schedules = schedules;
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

            subTasks.add(new BranchAndBoundRecursiveTask(firstPartitionedQueue, bestSchedule));
            subTasks.add(new BranchAndBoundRecursiveTask(secondPartitionedQueue, bestSchedule));
            //Fork then join 2 tasks, almost recursively iterate until set is split
            ForkJoinTask.invokeAll(subTasks)
                    .stream()
                    .map(ForkJoinTask::join).forEach(schedule -> {
                        if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
                            setBestSchedule(schedule);
                        }
                    });

        } else {
            processSchedule(schedules);
        }
        return bestSchedule;
    }

    private void processSchedule(PriorityQueue<Schedule> schedules) {
        logger.info("These schedules were processed by "
                + Thread.currentThread().getName());
        // Iterate through every schedule and work recursively on this
        Schedule schedule;
        while(schedules.size() > 0) {
            schedule = schedules.poll();
            if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
                calculateScheduleRecursive(schedules.poll());
            }
        }
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors);
        if (bestSchedule != null) {
            scheduler.setBestSchedule(bestSchedule);
        }
        scheduler.calculateSchedule(currentSchedule);
        Schedule schedule = scheduler.getBestSchedule();
        if (bestSchedule == null || schedule.getOverallTime() < bestSchedule.getOverallTime()) {
            setBestSchedule(schedule);
        }
    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    private static synchronized void setBestSchedule(Schedule schedule) {
        bestSchedule = schedule;
    }
}
