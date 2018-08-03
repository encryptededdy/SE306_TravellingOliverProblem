package uoa.se306.travellingoliverproblem.visualiser;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import uoa.se306.travellingoliverproblem.fileIO.DotFileWriter;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.SchedulerRunner;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphDrawer;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleDrawer;

import java.util.Map;

public class FXController {
    @FXML
    private Pane graphPane;

    @FXML
    private Pane schedulePane;

    private Map<Node, GraphNode> graphNodeMap;

    private void drawGraph(Graph graph) {
        GraphDrawer drawer = new GraphDrawer(graphPane, graph);
        drawer.drawGraph();
        graphNodeMap = drawer.getGraphNodes();
    }

    public void startProcessing(Graph inputGraph, int processors, String outputName) {
        Task<Void> task = SchedulerRunner.getInstance().startSchedulerJavaFXTask(inputGraph, processors);
        drawGraph(SchedulerRunner.getInstance().getInputGraph());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                SchedulerRunner.getInstance().printResult();
                drawSchedule(SchedulerRunner.getInstance().getSchedule());
                DotFileWriter fileWriter = new DotFileWriter(inputGraph, SchedulerRunner.getInstance().getSchedule(), outputName);
                fileWriter.outputSchedule();
            }
        });
        new Thread(task).start();
    }

    private void drawSchedule(Schedule schedule) {
        ScheduleDrawer drawer = new ScheduleDrawer(schedulePane, schedule, graphNodeMap);
        drawer.drawSchedule();
    }
}
