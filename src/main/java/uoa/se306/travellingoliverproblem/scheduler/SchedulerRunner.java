package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.TreeSet;

public class SchedulerRunner {
    private static SchedulerRunner ourInstance = new SchedulerRunner();
    private Graph inputGraph;
    private int noProcessors;
    private Schedule schedule;
    private ThreadListener tListener = null;
    // TODO: Add listeners

    public static SchedulerRunner getInstance() {
        return ourInstance;
    }

    private SchedulerRunner() {
    }

    public void startScheduler(Graph inputGraph, int noProcessors) {
        this.inputGraph = inputGraph;
        this.noProcessors = noProcessors;

        Scheduler scheduler = new DFSScheduler(inputGraph, noProcessors);
        // TODO: Multithread this
        Runnable scheduleTask = () -> {
            schedule = scheduler.getBestSchedule();
            System.out.println("Scheduler finished...");
            if(tListener != null){
                tListener.onScheduleFinish("hello");
            }
        };
        Thread scheduleThread = new Thread(scheduleTask);
        scheduleThread.start();
        System.out.println("Done!");

    }

    public void printResult() {
        ScheduledProcessor[] pro = schedule.getProcessors();
        for (int i = 0; i < pro.length; i++) {
            TreeSet<ScheduleEntry> nodeMap = pro[i].getFullSchedule();
            System.out.println("Processor " + Integer.toString(i) + " has tasks:" + nodeMap.toString());
        }
        System.out.println("The best overall time was: " + schedule.getOverallTime());
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
        public void onScheduleFinish(String t);
    }
}
