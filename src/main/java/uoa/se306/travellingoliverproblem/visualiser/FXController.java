package uoa.se306.travellingoliverproblem.visualiser;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphDrawer;

public class FXController {
    @FXML
    private Pane graphPane;

    public void drawGraph(Graph graph) {
        GraphDrawer drawer = new GraphDrawer(graphPane, graph);
        drawer.drawGraph();
    }
}
