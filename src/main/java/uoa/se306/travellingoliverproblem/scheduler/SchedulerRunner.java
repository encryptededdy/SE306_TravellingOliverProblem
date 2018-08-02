package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.PriorityQueue;
import java.util.TreeSet;

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

    private SchedulerRunner() {
    }

    public void startScheduler(Graph inputGraph, int noProcessors) {
        this.inputGraph = inputGraph;
        this.noProcessors = noProcessors;
      
        scheduler = new DFSScheduler(inputGraph, noProcessors);

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

    public void printResult() {
        ScheduledProcessor[] pro = schedule.getProcessors();
        for (int i = 0; i < pro.length; i++) {
            PriorityQueue<ScheduleEntry> nodeMap = pro[i].getFullSchedule();
            System.out.println("Processor " + Integer.toString(i) + " has tasks:" + nodeMap.toString());
        }
        System.out.println("The best overall time was: " + schedule.getOverallTime());
        System.out.printf("Out of %d branches, %d were pruned (%.1f%%)",
                scheduler.getBranchesConsidered()+scheduler.getBranchesKilled(),
                scheduler.getBranchesKilled(),
                scheduler.proportionKilled()*100);
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
