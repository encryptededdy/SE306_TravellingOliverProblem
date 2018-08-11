package uoa.se306.travellingoliverproblem.scheduler;

import gnu.trove.set.hash.THashSet;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.MinimalSchedule;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.GreedyBFS;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class DFSScheduler extends Scheduler {

    // useEquivalentScheduleCulling always enabled.
    private boolean useGreedyInitialSchedule = false; // Do we even need this anymore ?
    private boolean useExistingScheduleCleaner = true;

    private Set<MinimalSchedule> existingSchedules = new THashSet<>();

    private long startTime;

    public DFSScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        if (useGreedyInitialSchedule) {
            GreedyBFS greedyScheduler = new GreedyBFS();
            greedyScheduler.calculateGreedySchedule(new Schedule(currentSchedule));
            bestSchedule = greedyScheduler.getBestSchedule();
        }
        startTime = System.currentTimeMillis();
        calculateScheduleRecursive(currentSchedule);
    }

    private void cleanExistingSchedules() {
        long startTime = System.nanoTime();
        int previousSize = existingSchedules.size();
        existingSchedules.removeIf(minimalSchedule -> minimalSchedule.getCost() >= bestSchedule.getOverallTime()); // TODO: i don't know if checking the cost against end time is correct ?
        int cleaned = previousSize - existingSchedules.size();
        long endTime = System.nanoTime();
        System.out.println("Cleaning Took " + (endTime - startTime) / 1000000 + " ms, cleaned "+cleaned+" entries ("+(cleaned*100/previousSize)+"%)");
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        branchesConsidered++;
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getOverallTime() > currentSchedule.getOverallTime()) {
                System.out.println("Found new best schedule: "+currentSchedule.getOverallTime());
                System.out.println(currentSchedule.toString());
                bestSchedule = currentSchedule;
                // Only run cleaner if it's been at least 5 seconds since we started, otherwise there's no point
                if (useExistingScheduleCleaner && System.currentTimeMillis() > startTime + 5000) cleanExistingSchedules();
            }
            return;
        }

        PriorityQueue<Schedule> candidateSchedules = new PriorityQueue<>();

        for (Node node: currentSchedule.getAvailableNodes()) {
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

                //TODO: Im not sure if it's correct to drop  partial schedules just because it's cost is HIGHER than the current bestSchedules cost.
                //TODO: At this moment in time, a partial schedules cost might be bit higher than the current bestSchedule,
                //TODO: but later on, this partialSchedules (that we dropped) cost might get lower than our current bestSchedules cost.
                //TODO: Before, it was CORRECT to use the partial schedules overallTime against the current bestSchedule overallTime
                //TODO: This is because overallTime can only increase in value, while cost *might* decrease in value
                //TODO: If you expand a partial Schedule into its children partial Schedules, those children might have a lower cost than our current bestSchedule
                if (bestSchedule == null || tempSchedule.getCost() < bestSchedule.getCost()) {
                    candidateSchedules.add(tempSchedule);
                } else {
                    // drop this branch, because this partial schedule is guaranteed to be worse than what we currently have, based on overallTime
                    branchesKilled++;
                }
            }
        }
        /*
        while the local priorityQueue is not empty
            get first partialSchedule in the queue
            if the current bestSchedule is null OR partialSchedule.cost if smaller than bestSchedule.cost
                if the existingSchedules doesn't have the partialSchedule
                    add it to existingSchedule and call this recursiveMethod(partialSchedule)
                else
                    drop this partialSchedule because it's already been visited (duplicated)
            else
                break the while loop
         */
        while (!candidateSchedules.isEmpty()) {
            Schedule candidate = candidateSchedules.poll();
            //TODO: The way cost functions work is that, it is an estimate of how far away our partial schedule is from optimal.
            //TODO: If we keep dropping partial schedules because its current cost is a bit higher than our current bestSchedule,
            //TODO: we might accidentally drop a partial schedule that might become an optimal schedule later on (its children).
            //TODO: It's not as simple as checking if the candidate schedules cost is lower than current bestSchedules cost
            if (bestSchedule == null || candidate.getCost() < bestSchedule.getCost()) {
                // Only continue if this schedule hasn't been considered before
                MinimalSchedule minimal = new MinimalSchedule(candidate);
                if (!existingSchedules.contains(minimal)) {
                    existingSchedules.add(minimal);
                    calculateScheduleRecursive(candidate);
                } else {
                    branchesKilled++; // drop this branch
                    branchesKilledDuplication++;
                }
            } else {
                branchesKilled+=candidateSchedules.size()+1;
                break; // It's a priority queue, so we can just drop the rest
            }
        }
    }
}
