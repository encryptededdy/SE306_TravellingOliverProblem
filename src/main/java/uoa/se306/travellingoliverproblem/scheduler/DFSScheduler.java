package uoa.se306.travellingoliverproblem.scheduler;

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
    private boolean useCurrentBestCulling = true;
    private boolean useGreedyInitialSchedule = false;
    private boolean useLocalPriorityQueue = true;

    private Set<MinimalSchedule> existingSchedules = new HashSet<>();

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
        calculateScheduleRecursive(currentSchedule);
    }

    private void calculateScheduleRecursive(Schedule currentSchedule) {
        existingSchedules.add(new MinimalSchedule(currentSchedule)); // store this schedule as visited
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

            for (int j = 0; j < processors.length; j++) {

                int processorStartTime;
                ScheduledProcessor processor = processors[j];
                int startTime = 0;

                for (Node parentNode: node.getParents().keySet()) {

                    for (ScheduledProcessor checkProcessor: currentSchedule.getProcessors()) {

                        ScheduleEntry sEntry = checkProcessor.getEntry(parentNode);
                        if (sEntry != null) {
                            processorStartTime = sEntry.getEndTime();
                            // if the current processor doesn't have the parent node
                            processorStartTime += (processor != checkProcessor) ? parentNode.getChildren().get(node) : 0;
                            // if processor does not have a task yet, add the this node as the first task.
                            if (processorStartTime > startTime) {
                                startTime = processorStartTime;
                                break;
                            }
                        }
                    }
                }
                startTime = processor.getEarliestStartAfter(startTime, node.getCost());
                Schedule tempSchedule = new Schedule(currentSchedule);
                tempSchedule.addToSchedule(node, j, startTime);
                // Only continue if sub-schedule time is under upper bound
                // i.e. skip this branch if its overall time is already longer than the currently known best overall time
                if (!useCurrentBestCulling || bestSchedule == null || tempSchedule.getOverallTime() < bestSchedule.getOverallTime()) {
                    candidateSchedules.add(tempSchedule);
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
                    branchesKilled+=candidateSchedules.size()+1;
                    break; // It's a priority queue, so we can just drop the rest
                }
            }
        }
    }
}
