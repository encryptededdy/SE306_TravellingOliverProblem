package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDrawer {
    private static int SCHEDULE_WIDTH = 700;

    private Pane parentPane;
    private VBox vbox = new VBox();
    private List<HBox> processorRows = new ArrayList<>();
    private Schedule schedule;

    public ScheduleDrawer (Pane parentPane, Schedule schedule) {
        this.parentPane = parentPane;
        this.schedule = schedule;

        parentPane.getChildren().add(vbox);
    }

    public void drawSchedule() {
        int processorNo = 0;
        int totalTime = schedule.getOverallTime();

        // draw processors
        for (ScheduledProcessor p : schedule.getProcessors()) {
            HBox row = new HBox();
            row.setPrefHeight(30);
            row.setPadding(new Insets(50, 0, 0, 0));
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
                    int width = (int) ((gap / (double)totalTime) * SCHEDULE_WIDTH); // cast to int because truncation is fine
                    ScheduleNode node = new ScheduleNode(width);
                    row.getChildren().add(node);
                }
                // Draw entry
                int width = (int) ((e.getLength() / (double)totalTime) * SCHEDULE_WIDTH); // cast to int because truncation is fine
                ScheduleNode node = new ScheduleNode(e.toString(), e.getLength(), width);
                row.getChildren().add(node);
                lastScheduleEnd = e.getEndTime();
            }
            vbox.getChildren().add(row);
            processorNo++;
        }

        // draw super rough scale
        // TODO: don't use this it's bad
        HBox row = new HBox();
        row.setPrefHeight(30);
        row.setPadding(new Insets(30, 0, 0, 0));
        ScheduleNode header = new ScheduleNode("Scale");
        row.getChildren().add(header);
        for (int i = 1; i <= totalTime; i++) {
            int width = (int) ((1 / (double)totalTime) * SCHEDULE_WIDTH); // cast to int because truncation is fine
            ScheduleNode node = new ScheduleNode(Integer.toString(i), 0, width);
            row.getChildren().add(node);
        }
        vbox.getChildren().add(row);
        // end ugly code

    }
}
