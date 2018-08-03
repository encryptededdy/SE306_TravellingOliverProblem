package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleDrawer {
    public static int SCHEDULE_WIDTH = 700;
    public static int ROW_HEIGHT = 30;
    public static int HEADER_WIDTH = 100;

    private Pane parentPane;
    private VBox vbox = new VBox();
    private List<HBox> processorRows = new ArrayList<>();
    private Map<Node, GraphNode> graphNodes;
    private Schedule schedule;

    public ScheduleDrawer(Pane parentPane, Schedule schedule, Map<Node, GraphNode> graphNodes) {
        this.graphNodes = graphNodes;
        this.parentPane = parentPane;
        this.schedule = schedule;

        parentPane.getChildren().add(vbox);
    }

    public void drawSchedule() {
        int dividableWidth = SCHEDULE_WIDTH;
        int processorNo = 0;
        int totalTime = schedule.getOverallTime();

        // Calculate a width that's divisible by the width of the schedule
        if (totalTime <= SCHEDULE_WIDTH) {
            // only care if it's less
            dividableWidth = totalTime;
            while (dividableWidth + totalTime < SCHEDULE_WIDTH) {
                dividableWidth += totalTime;
            }
            if (dividableWidth < (SCHEDULE_WIDTH - SCHEDULE_WIDTH / 4)) {
                // if it's too small...
                dividableWidth = SCHEDULE_WIDTH;
            }
        }

        // draw processors
        for (ScheduledProcessor p : schedule.getProcessors()) {
            HBox row = new HBox();
            row.setMinHeight(30);
            row.setPadding(new Insets(10, 0, 0, 0));
            processorRows.add(processorNo, row);

            // draw header
            ScheduleNode header = new ScheduleNode("Processor "+processorNo);
            row.getChildren().add(header);

            // draw schedule
            int lastScheduleEnd = 0;
            for (ScheduleEntry e : p.getFullSchedule()) {
                if (e.getStartTime() > lastScheduleEnd) {
                    // Draw gap
                    int gap = e.getStartTime() - lastScheduleEnd;
                    double width = ((gap / (double) totalTime) * dividableWidth);
                    ScheduleNode node = new ScheduleNode(width);
                    row.getChildren().add(node);
                }
                // Draw entry
                double width = ((e.getLength() / (double) totalTime) * dividableWidth);
                ScheduleNode node = new ScheduleNode(width, e, graphNodes.get(e.getNode()));
                row.getChildren().add(node);
                lastScheduleEnd = e.getEndTime();
            }
            vbox.getChildren().add(row);
            processorNo++;
        }

        // calculate scale spacing
        int scaleSpacing;
        if (totalTime <= 25) {
            scaleSpacing = 1;
        } else if (totalTime <= 100) {
            scaleSpacing = 5;
        } else if (totalTime <= 150) {
            scaleSpacing = 10;
        } else {
            scaleSpacing = 25;
        }

        // draw scale
        HBox row = new HBox();
        row.setPrefHeight(30);
        row.setPadding(new Insets(50, 0, 0, 0));

        double width = ((1 / (double) totalTime) * dividableWidth); // width for each "1" time unit

        ScheduleNode header = new ScheduleNode(HEADER_WIDTH - width/2);
        row.getChildren().add(header);
        for (int i = 0; i <= totalTime; i++) {
            ScheduleScaleNode node;
            if (i % scaleSpacing == 0) {
                node = new ScheduleScaleNode(i, width);
                row.getChildren().add(node);
            } else {
                if ((i - 1) % scaleSpacing == 0) {
                    node = new ScheduleScaleNode(width * (scaleSpacing - 1));
                    row.getChildren().add(node);
                }
            }
        }
        vbox.getChildren().add(row);
    }
}
