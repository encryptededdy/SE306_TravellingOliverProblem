package uoa.se306.travellingoliverproblem.scheduler;

import javafx.concurrent.Task;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.parallel.BranchAndBoundRecursiveAction;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.ArrayList;

public class SchedulerRunner {
    private static SchedulerRunner ourInstance = new SchedulerRunner();
    private Graph inputGraph;
    private int noProcessors;
    private Schedule schedule;
    private Scheduler scheduler;
    private ThreadListener tListener = null;
    public static SchedulerRunner getInstance() {
        return ourInstance;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    private SchedulerRunner() {
    }

    public void startScheduler(Graph inputGraph, int noProcessors, boolean isParallelised, SchedulerType type) {
        this.inputGraph = inputGraph;
        this.noProcessors = noProcessors;

        scheduler = autoPickScheduler(inputGraph, noProcessors, isParallelised, type);

        // create task to run on a separate thread
        Runnable scheduleTask = () -> {
            long startTime = System.nanoTime();
            schedule = scheduler.getBestSchedule();
            long endTime = System.nanoTime();
            System.out.println("Took " + (endTime - startTime) / 1000000 + " ms");
            // trigger the thread listener
            if(tListener != null){
                tListener.onScheduleFinish();
            }
        };
        // run scheduleTask on a new thread
        Thread scheduleThread = new Thread(scheduleTask);
        scheduleThread.start();
    }

    public Task<Void> startSchedulerJavaFXTask(Graph inputGraph, int noProcessors, boolean isParallelised, SchedulerType type) {
        this.inputGraph = inputGraph;
        this.noProcessors = noProcessors;

        scheduler = autoPickScheduler(inputGraph, noProcessors, isParallelised, type);

        // create task to run on a separate thread
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long startTime = System.nanoTime();
                schedule = scheduler.getBestSchedule();
                long endTime = System.nanoTime();
                System.out.println("Took " + (endTime - startTime) / 1000000 + " ms");
                // trigger the thread listener
                if (tListener != null) {
                    tListener.onScheduleFinish();
                }
                return null;
            }
        };
    }

    public void printResult() {
        ScheduledProcessor[] pro = schedule.getProcessors();
        for (int i = 0; i < pro.length; i++) {
            ArrayList<ScheduleEntry> nodeMap = pro[i].getFullSchedule();
            System.out.println("Processor " + Integer.toString(i) + " has tasks:" + nodeMap.toString());
        }
        System.out.println("The best overall time was: " + schedule.getOverallTime());
        System.out.printf("Out of %d branches, %d were pruned (%.1f%%)",
                scheduler.getBranchesConsidered()+scheduler.getBranchesKilled(),
                scheduler.getBranchesKilled(),
                scheduler.proportionKilled()*100);
    }

    private Scheduler autoPickScheduler(Graph inputGraph, int noProcessors, boolean isParallelised, SchedulerType type) {
        if (isParallelised) {
            // ForkJoinPool has already set up the number of threads to run
            System.out.println("Input graph has " + inputGraph.getAllNodes().size() + " nodes. Using DFS/BnB Parallel scheduling algorithm");
            return new ParallelScheduler(inputGraph, noProcessors, false, isParallelised);
        } else {
            if (type != null) {
                switch (type) {
                    case ASTAR:
                        System.out.println("Using A* Scheduler");
                        return new AStarSearchScheduler(inputGraph, noProcessors, isParallelised);
                    case DFS:
                        System.out.println("Using DFS Scheduler");
                        return new DFSScheduler(inputGraph, noProcessors, isParallelised);
                    case HYBRID:
                        System.out.println("Using Hybrid Scheduler (BETA). Note that Visualisation is not supported in this mode.");
                        return new HybridScheduler(inputGraph, noProcessors, isParallelised, 1000);
                    default:
                        return new DFSScheduler(inputGraph, noProcessors, isParallelised);
                }
            } else if (inputGraph.getAllNodes().size() < 10 && !inputGraph.getAllNodes().stream().allMatch(Node::isIndependent)) {
                System.out.println("Input graph has " + inputGraph.getAllNodes().size() + " nodes. Using A* scheduling algorithm");
                return new AStarSearchScheduler(inputGraph, noProcessors, isParallelised);
            } else {
                System.out.println("Input graph has " + inputGraph.getAllNodes().size() + " nodes. Using DFS/BnB scheduling algorithm");
                return new DFSScheduler(inputGraph, noProcessors, isParallelised);
            }
        }
    }

    public Graph getInputGraph() {
        return inputGraph;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setThreadListener(ThreadListener listener){
        this.tListener = listener;
    }

    public interface ThreadListener{
        public void onScheduleFinish();
    }
}
