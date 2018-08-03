package uoa.se306.travellingoliverproblem.visualiser;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.fileIO.DotFileWriter;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.SchedulerRunner;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphDrawer;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleDrawer;

import java.util.List;
import java.util.Map;

public class FXController {
    @FXML
    private Pane graphPane;

    @FXML
    private Pane schedulePane;

    @FXML
    private HBox tilesBox;

    private Map<Node, GraphNode> graphNodeMap;
    private Timeline timeline;

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
                timeline.stop();
                SchedulerRunner.getInstance().printResult();
                drawSchedule(SchedulerRunner.getInstance().getSchedule());
                DotFileWriter fileWriter = new DotFileWriter(inputGraph, SchedulerRunner.getInstance().getSchedule(), outputName);
                fileWriter.outputSchedule();
            }
        });
        new Thread(task).start();
        startPolling();
    }

    private void startPolling() {
        // Setup tiles
        Tile memoryTile = TileBuilder.create().skinType(Tile.SkinType.BAR_GAUGE)
                .title("Memory usage")
                .unit("MB")
                .maxValue(4000)
                .animated(true)
                .build();

        Tile generatedBranches = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .title("Branches Generated")
                .decimals(0)
                .build();

        Tile boundedBranches = TileBuilder.create().skinType(Tile.SkinType.CIRCULAR_PROGRESS)
                .title("Branches Bounded")
                .decimals(2)
                .build();

        tilesBox.getChildren().addAll(memoryTile, generatedBranches, boundedBranches);
        timeline = new Timeline(new KeyFrame(Duration.millis(1500), event -> {
            // Update statistics
            double memoryUse = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000d;
            memoryTile.setValue(memoryUse);
            generatedBranches.addChartData(new ChartData(SchedulerRunner.getInstance().getScheduler().getBranchesConsidered() + SchedulerRunner.getInstance().getScheduler().getBranchesKilled()));
            boundedBranches.setValue(SchedulerRunner.getInstance().getScheduler().proportionKilled()*100);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void drawSchedule(Schedule schedule) {
        ScheduleDrawer drawer = new ScheduleDrawer(schedulePane, schedule, graphNodeMap);
        drawer.drawSchedule();
    }
}
