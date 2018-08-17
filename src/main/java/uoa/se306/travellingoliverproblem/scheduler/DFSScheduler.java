package uoa.se306.travellingoliverproblem.scheduler;

import gnu.trove.set.hash.THashSet;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.MinimalSchedule;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

public class DFSScheduler extends Scheduler {

    // useEquivalentScheduleCulling always enabled.
    private boolean useExistingScheduleCleaner = true;
    private boolean localDuplicateDetectionOnly = false;
    private Set<MinimalSchedule> existingSchedules = new THashSet<>();
    private long startTime;

    // For Parallel
    private static Set<MinimalSchedule> existingParallelSchedules = new THashSet<>();
    private Set<Schedule> unfinishedSchedules = new HashSet<>();

    private static final int MAX_MEMORY = 20000000;
    
    public DFSScheduler(Graph graph, int amountOfProcessors, boolean IsParallelised) {
        super(graph, amountOfProcessors, true, IsParallelised);
    }

    @Override
    public void calculateSchedule(Schedule currentSchedule) {
        startTime = System.currentTimeMillis();
        calculateScheduleRecursive(currentSchedule);
    }

    public Schedule calculateScheduleParallel(Schedule currentSchedule) {
        startTime = System.currentTimeMillis();
        calculateScheduleRecursive(currentSchedule);
        return bestSchedule;
    }

    private void cleanExistingSchedules() {
        long startTime = System.nanoTime();
        int cleaned;
        int previousSize;
        if (isParallelised) {
            previousSize = getQueueSize();
            removeIfQueue(minimalSchedule -> minimalSchedule.getCost() >= bestSchedule.getCost());
            cleaned = previousSize - getQueueSize();
        } else {
            previousSize = existingSchedules.size();
            existingSchedules.removeIf(minimalSchedule -> minimalSchedule.getCost() >= bestSchedule.getCost());
            cleaned = previousSize - existingSchedules.size();
        }
        long endTime = System.nanoTime();
        System.out.println("Cleaning Took " + (endTime - startTime) / 1000000 + " ms, cleaned " + cleaned + " entries (" + (cleaned * 100 / previousSize) + "%)");
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        branchesConsidered++;
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getCost() > currentSchedule.getCost()) {
                System.out.println("Found new best schedule: " + currentSchedule.getOverallTime());
                bestSchedule = currentSchedule;
                // Only run cleaner if it's been at least 5 seconds since we started, otherwise there's no point
                if (!localDuplicateDetectionOnly && useExistingScheduleCleaner && System.currentTimeMillis() > startTime + 5000)
                    cleanExistingSchedules();
            }
            return;
        }

        PriorityQueue<Schedule> candidateSchedules = new PriorityQueue<>();
        // Normal Scheduling
        for (Node node : currentSchedule.getAvailableNodes()) {
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

            for (int j = 0; j < processors.length; j++) {
                int startTime = processors[j].getEarliestStartAfter(processorEarliestAvailable[j], node.getCost());
                Schedule tempSchedule = new Schedule(currentSchedule);
                tempSchedule.addToSchedule(node, j, startTime);
                // Only continue if sub-schedule time is under upper bound
                // i.e. skip this branch if its overall time is already longer than the currently known best overall time
                if (bestSchedule == null || tempSchedule.getCost() < bestSchedule.getCost()) {
                    candidateSchedules.add(tempSchedule);
                } else {
                    // drop this branch, because this partial schedule is guaranteed to be worse than what we currently have, based on overallTime
                    branchesKilled++;
                }
            }
        }

        // used for local duplicate detection
        Set<MinimalSchedule> consideredThisRound = new HashSet<>();

        while (!candidateSchedules.isEmpty()) {
            Schedule candidate = candidateSchedules.poll();
            if (bestSchedule == null || candidate.getCost() < bestSchedule.getCost()) {
                // Only continue if this schedule hasn't been considered before
                MinimalSchedule minimal = new MinimalSchedule(candidate);
                if (localDuplicateDetectionOnly && !consideredThisRound.contains(minimal)) {
                    consideredThisRound.add(minimal);
                    recursiveIfNotParallel(candidate);
                } else if (!localDuplicateDetectionOnly && !isParallelised && !existingSchedules.contains(minimal)) {
                    if (existingSchedules.size() < MAX_MEMORY) existingSchedules.add(minimal);
                    recursiveIfNotParallel(candidate);
                } else if (!localDuplicateDetectionOnly && isParallelised) {
                    if (getQueueSize() < MAX_MEMORY && checkThenAddToQueue(minimal)) {
                        recursiveIfNotParallel(candidate);
                    }
                } else {
                    branchesKilled++; // drop this branch
                    branchesKilledDuplication++;
                }
            } else {
                branchesKilled += candidateSchedules.size() + 1;
                break; // It's a priority queue, so we can just drop the rest
            }
        }
    }

    private void recursiveIfNotParallel(Schedule schedule) {
        if (isParallelised) {
            unfinishedSchedules.add(schedule);
        } else {
            calculateScheduleRecursive(schedule);
        }
    }


    public Set<Schedule> getUnfinishedSchedules() {
        return unfinishedSchedules;
    }

    // Check if minimum schedule is in the queue, if it isnt add it, else dont
    private static synchronized boolean checkThenAddToQueue(MinimalSchedule mSchedule) {
        // Contains first as add is more expensive
        if (!existingParallelSchedules.contains(mSchedule)) {
            existingParallelSchedules.add(mSchedule);
            return true;
        }
        return false;
    }

    //Get the queue size concurrently to ensure values are correct at the time
    private static synchronized int getQueueSize() {
        return existingParallelSchedules.size();
    }

    //Remove from queue based on a predicate input (Usually checking for larger schedules)
    private static synchronized void removeIfQueue(Predicate<MinimalSchedule> filter) {
        existingParallelSchedules.removeIf(filter);
    }

    @Deprecated
    public static void reset() {
        existingParallelSchedules = new THashSet<>();
    }

}

