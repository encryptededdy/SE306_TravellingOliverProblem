package uoa.se306.travellingoliverproblem.visualiser;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphDrawer;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleDrawer;

public class FXController {
    @FXML
    private Pane graphPane;

    @FXML
    private Pane schedulePane;

    public void drawGraph(Graph graph) {
        GraphDrawer drawer = new GraphDrawer(graphPane, graph);
        drawer.drawGraph();
    }

    public void drawSchedule(Schedule schedule) {
        ScheduleDrawer drawer = new ScheduleDrawer(schedulePane, schedule);
        drawer.drawSchedule();
    }
}
