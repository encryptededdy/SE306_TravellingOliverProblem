package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashSet;
import java.util.Set;

public class DFSScheduler extends Scheduler {

    public DFSScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        // If the currentSchedule has no available nodes
        if (currentSchedule.getAvailableNodes().isEmpty()) {
            // If our bestSchedule is null or the overall time for the bestSchedule is less than our current schedule
            if (bestSchedule == null || bestSchedule.getOverallTime() < currentSchedule.getOverallTime()) {
                bestSchedule = currentSchedule;
            }
            return;
        }

        // Fix iterator issues
        Set<Node> tempSet = new HashSet<>(currentSchedule.getAvailableNodes());
        for (Node node: tempSet) {
            // Get the amount of processors in the current schedule
            ScheduledProcessor[] processors = currentSchedule.getProcessors();
            // for all the processors in the current schedule
            for (int j = 0; j < processors.length; j++) {
                // get that processor
                ScheduledProcessor processor = processors[j];
                int startTime = 0;
                // for all the parent nodes of the current available node
                for (Node parentNode: node.getParents().keySet()) {
                    int processorStartTime;
                    // for all the processors of this current schedule
                    for (ScheduledProcessor checkProcessor: currentSchedule.getProcessors()) {
                        // if any of the processors have processed the parent node
                        if (checkProcessor.contains(parentNode)) {
                            //get the parent node
                            ScheduleEntry sEntry = checkProcessor.getEntry(parentNode);
                            // get the end time of the parent node
                            processorStartTime = sEntry.getEndTime();
                            // if the current processor doesn't have the parent node
                            if (checkProcessor != processor) {
                                // adds the execution to the total processorStartTime
                                processorStartTime += parentNode.getChildren().get(node);
                            }
                            // if processor does not have a task yet, add the this node as the first task.
                            if (processorStartTime > startTime) {
                                startTime = processorStartTime;
                            }
                        }
                    }
                }
                startTime = processor.getEarliestStartAfter(startTime, node.getCost());
                currentSchedule.addToSchedule(node, j, startTime);
                calculateSchedule(currentSchedule);//recursive
            }
        }
    }
}
