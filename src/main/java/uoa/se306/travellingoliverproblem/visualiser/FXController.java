package uoa.se306.travellingoliverproblem.visualiser;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.fileIO.DotFileWriter;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.SchedulerRunner;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphDrawer;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;
import uoa.se306.travellingoliverproblem.visualiser.graph.SequentialGraphDrawer;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleDrawer;

import java.util.ArrayList;
import java.util.Map;

import static uoa.se306.travellingoliverproblem.scheduler.Scheduler.COMPUTATIONAL_LOAD;

public class FXController {
    @FXML
    private Pane graphPane;

    @FXML
    private Pane schedulePane;

    @FXML
    private HBox tilesBox;

    @FXML
    private GridPane tilesGrid;

    @FXML
    private Text statusText;

    private Map<Node, GraphNode> graphNodeMap;
    private Timeline pollingTimeline;
    private Timeline timerTimeline;
    private long lastBranches = 0;
    private Schedule lastSchedule;
    long startTime;
    private GridPane statusPane;
    private ColourScheme paleChristmas = new ColourScheme("#fceade", "#f48b94", "#f7a7a6", "#ffffff", "#acdbc9", "#dbebc2", "#ffffff");


    private void drawGraph(Graph graph) {
        SequentialGraphDrawer drawer = new SequentialGraphDrawer(graphPane, graph, graphScrollPane, this.paleChristmas);
        drawer.drawGraph();
        graphNodeMap = drawer.getGraphNodes();
    }

    public void startProcessing(Graph inputGraph, int processors, String outputName) {
        Task<Void> task = SchedulerRunner.getInstance().startSchedulerJavaFXTask(inputGraph, processors);
        drawGraph(SchedulerRunner.getInstance().getInputGraph());
        startTime = System.currentTimeMillis();
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                timerTimeline.stop();
                long endTime = System.currentTimeMillis();
                statusText.setText("Done, took " + (endTime - startTime) + " ms");
                pollingTimeline.setCycleCount(1);
                pollingTimeline.playFromStart();
                SchedulerRunner.getInstance().printResult();
                drawSchedule(SchedulerRunner.getInstance().getSchedule());
                scheduleTitleText.setText("Best Schedule");
                DotFileWriter fileWriter = new DotFileWriter(inputGraph, SchedulerRunner.getInstance().getSchedule(), outputName);
                fileWriter.outputSchedule();
            }
        });
        new Thread(task).start();
        startTimer();
        startPolling();
    }

    private void startTimer() {
        timerTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            Duration time = Duration.millis(System.currentTimeMillis() - startTime);
            int minutes = (int) time.toMinutes();
            int seconds = (int) time.toSeconds() - minutes * 60;
            statusText.setText(String.format("Running: %02dm %02ds elapsed", minutes, seconds));
        }));
        timerTimeline.setCycleCount(Animation.INDEFINITE);
        timerTimeline.play();
    }

    private void startPolling() {
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.GRAY);

        // Setup tiles

        /* STATUS PANE CODE */
        statusPane = new GridPane();
        //statusPane.setGridLinesVisible(true);
        statusPane.setMinSize(480, 230);
        statusPane.setPrefSize(480, 230);
        statusPane.setMaxSize(480, 230);
        statusPane.setEffect(ds);
        statusPane.setBackground(new Background(new BackgroundFill(paleChristmas.backgroundColor, new CornerRadii(4), new Insets(0))));
        GridPane.setConstraints(statusPane, 0, 0, 2, 1);

        Text statusTextLabel = new Text("Status: ");
        GridPane.setConstraints(statusTextLabel, 0,1);
        statusText = new Text("Starting up...");
        GridPane.setConstraints(statusText, 1,1);

        Text branchRateTextLabel = new Text("Branches/sec: ");
        GridPane.setConstraints(branchRateTextLabel, 0,2);
        Text branchRateText = new Text("0");
        GridPane.setConstraints(branchRateText, 1,2);

        Text memoryUsageTextLabel = new Text("Memory Usage: ");
        GridPane.setConstraints(memoryUsageTextLabel, 0,3);
        Text memoryUsageText = new Text("0 MB");
        GridPane.setConstraints(memoryUsageText, 1,3);

        Text branchesConsideredTextLabel = new Text("Branches Considered: ");
        GridPane.setConstraints(branchesConsideredTextLabel, 0,4);
        Text branchesConsideredText = new Text("0 Branches");
        GridPane.setConstraints(branchesConsideredText, 1,4);

        Text fileNameTextLabel = new Text("Filename: ");
        GridPane.setConstraints(fileNameTextLabel, 0,5);
        Text fileNameText = new Text();
        GridPane.setConstraints(fileNameText, 1,5);

        Text threadsTextLabel = new Text("Number of Threads: ");
        GridPane.setConstraints(threadsTextLabel, 0,6);
        Text threadsText = new Text();
        GridPane.setConstraints(threadsText, 1,6);

        ArrayList<Text> statusTexts = new ArrayList<>();
        statusTexts.add(statusTextLabel);
        statusTexts.add(statusText);
        statusTexts.add(branchRateTextLabel);
        statusTexts.add(branchRateText);
        statusTexts.add(memoryUsageTextLabel);
        statusTexts.add(memoryUsageText);
        statusTexts.add(branchesConsideredTextLabel);
        statusTexts.add(branchesConsideredText);
        statusTexts.add(fileNameTextLabel);
        statusTexts.add(fileNameText);
        statusTexts.add(threadsTextLabel);
        statusTexts.add(threadsText);

        statusTexts.forEach(text -> {
            text.setFont(new Font("Roboto", 18));
            statusPane.setMargin(text, new Insets(5));
        });
        statusPane.getChildren().addAll(statusTexts);


        /* END OF STATUS PANE CODE */

        Tile generatedBranches = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .title("Branches Generated")
                .decimals(0)
                .minWidth(230).maxWidth(230)
                .minHeight(230).maxHeight(230)
                .chartData(new ChartData(0), new ChartData(0))
                .animated(false)
                .smoothing(true)
                .build();
        GridPane.setConstraints(generatedBranches,0, 2);

        Tile boundedBranches = TileBuilder.create().skinType(Tile.SkinType.DONUT_CHART)
                .title("Branch Bound Ratio")
                .decimals(0)
                .animated(true)
                .minWidth(480).maxWidth(480)
                .minHeight(230).maxHeight(230)
                .build();
        GridPane.setConstraints(boundedBranches, 0, 3, 2, 1);

        Tile bestTime = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .title("Current best schedule length")
                .decimals(0)
                .chartData(new ChartData(COMPUTATIONAL_LOAD), new ChartData(COMPUTATIONAL_LOAD))
                .minWidth(230).maxWidth(230)
                .minHeight(230).maxHeight(230)
                .smoothing(true)
                .build();
        GridPane.setConstraints(bestTime, 1, 2);


        tilesGrid.setHgap(0);
        //tilesGrid.setGridLinesVisible(true);
        tilesGrid.setEffect(ds);
        tilesGrid.setMargin(statusPane, new Insets(10));
        tilesGrid.setMargin(generatedBranches, new Insets(10));
        tilesGrid.setMargin(bestTime, new Insets(10));
        tilesGrid.setMargin(boundedBranches, new Insets(10));



        tilesGrid.getChildren().addAll(generatedBranches, bestTime, boundedBranches, statusPane);
        pollingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            // Check for new schedule
            if (lastSchedule == null || !lastSchedule.equals(SchedulerRunner.getInstance().getScheduler().getCurrentBestSchedule())) {
                lastSchedule = SchedulerRunner.getInstance().getScheduler().getCurrentBestSchedule();
                drawSchedule(lastSchedule);
                bestTime.addChartData(new ChartData(lastSchedule.getOverallTime()));
            }
            // Update statistics
            double memoryUse = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000d;
            long totalBranches = SchedulerRunner.getInstance().getScheduler().getBranchesConsidered() + SchedulerRunner.getInstance().getScheduler().getBranchesKilled();
            memoryUsageText.setText(Integer.toString((int) memoryUse) + "MB/4000MB");
            generatedBranches.addChartData(new ChartData(totalBranches));
            branchRateText.setText(Long.toString(totalBranches - lastBranches));
            branchesConsideredText.setText(Long.toString(SchedulerRunner.getInstance().getScheduler().getBranchesConsidered()) + " Branches");
            // Setup data for pie chart
            ChartData cd1 = new ChartData("Considered", SchedulerRunner.getInstance().getScheduler().getBranchesConsidered(), Tile.GREEN);
            ChartData cd2 = new ChartData("Pruned", SchedulerRunner.getInstance().getScheduler().getBranchesKilled() - SchedulerRunner.getInstance().getScheduler().getBranchesKilledDuplication(), Tile.BLUE);
            ChartData cd3 = new ChartData("Duplicates", SchedulerRunner.getInstance().getScheduler().getBranchesKilledDuplication(), Tile.YELLOW_ORANGE);
            boundedBranches.setChartData(cd1, cd2, cd3);
            lastBranches = totalBranches;
        }));
        pollingTimeline.setCycleCount(Animation.INDEFINITE);
        pollingTimeline.play();
    }

    private void drawSchedule(Schedule schedule) {
        schedulePane.getChildren().clear();
        ScheduleDrawer drawer = new ScheduleDrawer(schedulePane, schedule, graphNodeMap, paleChristmas);
        drawer.drawSchedule();
    }
}
