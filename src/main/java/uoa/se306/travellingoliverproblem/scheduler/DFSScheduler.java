package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.GreedyBFS;

import java.util.HashSet;
import java.util.Set;

public class DFSScheduler extends Scheduler {

    private boolean useEquivalentScheduleCulling = true;
    private boolean useCurrentBestCulling = true;
    private boolean useGreedyInitialSchedule = true;

    private Set<String> existingSchedules = new HashSet<>();

    DFSScheduler(Graph graph, int amountOfProcessors) {
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
        existingSchedules.add(currentSchedule.toString()); // store this schedule as visited
        branchesConsidered++;
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getOverallTime() > currentSchedule.getOverallTime()) {
                bestSchedule = currentSchedule;
            }
            return;
        }

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

                        if (checkProcessor.contains(parentNode)) {

                            ScheduleEntry sEntry = checkProcessor.getEntry(parentNode);

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
                if (!useCurrentBestCulling || bestSchedule == null || tempSchedule.getOverallTime() <= bestSchedule.getOverallTime()) {
                    if (useEquivalentScheduleCulling) {
                        // Only continue if this schedule hasn't been considered before
                        if (!existingSchedules.contains(tempSchedule.toString())) {
                            calculateScheduleRecursive(tempSchedule);
                        } else {
                            branchesKilled++; // drop this branch
                        }
                    } else {
                        calculateScheduleRecursive(tempSchedule);//recursive
                    }
                } else {
                    branchesKilled++; // drop this branch
                }
            }
        }
    }
}
