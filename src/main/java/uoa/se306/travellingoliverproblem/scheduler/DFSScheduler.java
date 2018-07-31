package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashSet;
import java.util.Set;

public class DFSScheduler extends Scheduler {

    private boolean useEquivalentScheduleCulling = true;
    private boolean useCurrentBestCulling = true;

    public DFSScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
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
            Set<Schedule> tempSchedules = new HashSet<>();
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
                        tempSchedules.add(tempSchedule);
                    } else {
                        calculateSchedule(tempSchedule);//recursive
                    }
                }
            }
            if (useEquivalentScheduleCulling) {
                for (Schedule s : tempSchedules) {
                    calculateSchedule(s);
                }
            }
        }
    }
}
