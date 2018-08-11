package uoa.se306.travellingoliverproblem.parallel;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.MinimalSchedule;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class BranchAndBoundRecursiveAction extends RecursiveAction {

    private Set<Schedule> schedules;
    private static final int SEQUENTIAL_THRESHOLD = 8;
    private static Schedule bestSchedule;
    protected int amountOfProcessors = 2;//Change later
    private boolean useCurrentBestCulling = true;
    private boolean useGreedyInitialSchedule = false;
    private boolean useLocalPriorityQueue = true;

    private long branchesConsidered = 0;
    private long branchesKilled = 0;
    private ConcurrentLinkedQueue queuedSchedules = new ConcurrentLinkedQueue();
    //No concurrent set, have to derive from hashmap
    private ConcurrentHashMap<MinimalSchedule, Void> existingSchedules = new ConcurrentHashMap<>();

    public BranchAndBoundRecursiveAction(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    protected void compute() {
        if (schedules.size() > SEQUENTIAL_THRESHOLD) {
            List<BranchAndBoundRecursiveAction> subTasks = new ArrayList<>();


            // Split Set
//            String partOne = schedules.substring(0, schedules.length() / 2);
//            String partTwo = workLoad.substring(workLoad.length() / 2, workLoad.length());

            subTasks.add(new BranchAndBoundRecursiveAction(schedules));
            subTasks.add(new BranchAndBoundRecursiveAction(schedules));
            ForkJoinTask.invokeAll(subTasks);
        } else {
            processSchedule(schedules);
        }
    }

    private void processSchedule(Set<Schedule> schedules) {
        for(Schedule schedule: schedules) {
            calculateScheduleRecursive(schedule);
        }
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {

        Void voidObj;
        try {
            Constructor<Void> constructor = (Constructor<Void>) Void.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            voidObj =  constructor.newInstance();
            existingSchedules.put(new MinimalSchedule(currentSchedule), voidObj);
        } catch(Exception e) {

        }// store this schedule as visited
        branchesConsidered++;
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getOverallTime() > currentSchedule.getOverallTime()) {
                System.out.println("Found new best schedule: "+currentSchedule.getOverallTime());
                bestSchedule = currentSchedule;
            }
            return;
        }

        PriorityQueue<Schedule> candidateSchedules = new PriorityQueue<>();
        // Fix iterator issues
        Set<Node> tempSet = new HashSet<>(currentSchedule.getAvailableNodes());
        for (Node node: tempSet) {
            // Get the amount of processors in the current schedule
            ScheduledProcessor[] processors = currentSchedule.getProcessors();
            int[] processorEarliestAvailable = new int[processors.length];

            // First, calculate the next available time on all nodes, taking into account parents
            for (Node parentNode : node.getParents().keySet()) {
                for (int i = 0; i < processors.length; i++) {
                    ScheduleEntry sEntry = processors[i].getEntry(parentNode);
                    if (sEntry != null) { // if this parent
                        // Get end time of parent, and the communication cost
                        int endTime = sEntry.getEndTime();
                        int communication = sEntry.getNode().getChildren().get(node);

                        for (int j = 0; j < processors.length; j++) { // for each entry in the processorEarliestAvailable array
                            if (j == i) { // If same proc (no comm cost)
                                if (processorEarliestAvailable[j] < endTime) {
                                    processorEarliestAvailable[j] = endTime;
                                }
                            } else { // not same proc (comm cost!)
                                if (processorEarliestAvailable[j] < endTime + communication) {
                                    processorEarliestAvailable[j] = endTime + communication;
                                }
                            }
                        }
                        break; // A node cannot be scheduled in multiple processors
                    }
                }
            }

            // Then, for each processor we can put this node on, actually put it on.
            for (int j = 0; j < processors.length; j++) {
                int startTime = processors[j].getEarliestStartAfter(processorEarliestAvailable[j], node.getCost());
                Schedule tempSchedule = new Schedule(currentSchedule); // make a new subschedule with the new node
                tempSchedule.addToSchedule(node, j, startTime);
                // Only continue if sub-schedule time is under upper bound
                // i.e. skip this branch if its overall time is already longer than the currently known best overall time
                if (!useCurrentBestCulling || bestSchedule == null || tempSchedule.getOverallTime() < bestSchedule.getOverallTime()) {
                    candidateSchedules.add(tempSchedule); // Add the new subschedule to the queue
                } else {
                    branchesKilled++; // drop this branch
                }
            }
        }
        if (useLocalPriorityQueue) {
            while (!candidateSchedules.isEmpty()) {
                Schedule candidate = candidateSchedules.poll();
                if (bestSchedule == null || candidate.getOverallTime() < bestSchedule.getOverallTime()) {
                    // Only continue if this schedule hasn't been considered before
                    if (!existingSchedules.contains(new MinimalSchedule(candidate))) {
                        calculateScheduleRecursive(candidate);
                    } else {
                        branchesKilled++; // drop this branch
                    }
                } else {
                    branchesKilled += candidateSchedules.size() + 1;
                    break; // It's a priority queue, so we can just drop the rest
                }
            }
        }
    }

}
