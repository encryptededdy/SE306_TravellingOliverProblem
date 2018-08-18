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

/*
Scheduler for the A Star Algorithm
 */
public class HybridScheduler extends Scheduler {

    private Set<MinimalSchedule> existingSchedules = new THashSet<>();
    private PriorityQueue<Schedule> candidateSchedules = new PriorityQueue<>();
    private PriorityQueue<Schedule> readySchedules = new PriorityQueue<>();
    private int hybridMaxSize;

    private static final int HYBRID_MAX_DEPTH = 4;
    private static final boolean USE_DEPTH_LIMIT = false;

    public HybridScheduler(Graph graph, int amountOfProcessors, boolean isParallelised, int maxSize) {
        super(graph, amountOfProcessors, false, isParallelised);
        this.hybridMaxSize = maxSize;
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        System.out.println("Hybrid config: MAX_DEPTH = " + HYBRID_MAX_DEPTH + ", MAX_SIZE = " + hybridMaxSize + ", USE_DEPTH_LIMIT = " + USE_DEPTH_LIMIT);
        solveAStar(currentSchedule);
    }

    private void solveAStar(Schedule currentSchedule) {
        branchesConsidered++;
        candidateSchedules.add(currentSchedule);
        existingSchedules.add(new MinimalSchedule(currentSchedule)); // store this schedule as visited

        while (true) {

            // if it's empty, then it's time to switch to DFS!
            if (USE_DEPTH_LIMIT && candidateSchedules.isEmpty()) {
                existingSchedules = null;// clear ExistingSchedules from Memory
                if (!isParallelised) {
                    beginDFS();
                }
                break;
            } else if (!USE_DEPTH_LIMIT && candidateSchedules.size() > hybridMaxSize) {
                existingSchedules = null; // clear ExistingSchedules from Memory
                readySchedules = candidateSchedules;
                if (!isParallelised) {
                    beginDFS();
                }
                break;
            }

            Schedule partial = candidateSchedules.poll();

            // Oh, we found the optimal solution!
            if (partial.getAvailableNodes().isEmpty()) {
                bestSchedule = partial;
                break;
            } else {
                // Get all the available nodes in the schedule
                Set<Node> availableNodes = new HashSet<>(partial.getAvailableNodes());
                for (Node node : availableNodes) {
                    ScheduledProcessor[] processors = partial.getProcessors();
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
                        MinimalSchedule m = partial.testAddToSchedule(node, j, startTime);

                        if (!existingSchedules.contains(m)) {
                            branchesConsidered++;
                            //create a copy of our partialSchedule
                            //add the availableNode into processor i at time startTime in the schedule
                            Schedule tempSchedule = new Schedule(partial);
                            tempSchedule.addToSchedule(node, j, startTime);
                            existingSchedules.add(new MinimalSchedule(tempSchedule)); // existingSchedules needs cost data
                            if (USE_DEPTH_LIMIT && (graph.getAllNodes().size() - tempSchedule.getUnAddedNodes().size()) >= HYBRID_MAX_DEPTH) {
                                readySchedules.add(tempSchedule); // put this in readySchedules to prepare it for DFS
                            } else {
                                candidateSchedules.add(tempSchedule); // otherwise continue A*
                            }
                        } else {
                            branchesKilled++;
                            branchesKilledDuplication++;
                        }
                    }
                }
            }
        }
    }

    // Move all current schedules to run on DFSScheduler until finished
    private void beginDFS() {
        System.out.println("Switching to DFS from A*");
        System.out.println(readySchedules.size());
        while (!readySchedules.isEmpty()) {
            Schedule schedule = readySchedules.poll();
            if (bestSchedule == null || schedule.getCost() < bestSchedule.getCost()) {
                DFSScheduler scheduler = new DFSScheduler(graph, amountOfProcessors, isParallelised);
                scheduler.bestSchedule = bestSchedule;
                System.out.println("Computing Schedule... " + (readySchedules.size() + 1) + " remaining, size " + (graph.getAllNodes().size() - schedule.getUnAddedNodes().size()));
                scheduler.calculateSchedule(schedule);
                if (bestSchedule == null || bestSchedule.getOverallTime() > scheduler.getBestSchedule().getOverallTime()) {
                    bestSchedule = scheduler.getBestSchedule();
                }
            } else {
                System.out.println((readySchedules.size() + 1) + " schedules dropped (not passed to DFS)");
                break; // It's a priority queue, so we don't need to see anymore.
            }
        }
    }

    // For parallelisation
    public PriorityQueue<Schedule> getSchedules() {
        return readySchedules;
    }
}
