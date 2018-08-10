package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.LinkedHashMap;
import java.util.Map;

public class SequentialGraphDrawer {
    private Pane parentPane;
    private Pane backgroundPane = new Pane();
    private VBox vbox = new VBox();
    private Graph graph;
    private LinkedHashMap<Node, GraphNode> visited = new LinkedHashMap<>();
    private ScrollPane sp;

    public SequentialGraphDrawer(Pane parentPane, Graph graph, ScrollPane graphScrollPane) {
        this.sp = graphScrollPane;
        this.parentPane = parentPane;
        this.graph = graph;
        backgroundPane.setMinWidth(600);
        StackPane sp = new StackPane(backgroundPane, vbox);
        // populate pane
        parentPane.getChildren().add(sp);
        // TODO: Deal with vbox width / centering
    }

    public Map<Node, GraphNode> getGraphNodes() {
        return visited;
    }

    public void drawGraph() {
        drawLevels();

        // hacky delay
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(event -> drawLines());
        pt.play();
    }

    private void drawLevels() {
        // Draw orphans first
        for (Node n : graph.getAllNodes()) {
            if (!visited.containsKey(n) && n.getChildren().isEmpty() && n.getParents().isEmpty()) {
                // if all parents are already placed, place this node
                addNode(n);
            }
        }

        while (visited.size() < graph.getAllNodes().size()) {
            for (Node n : graph.getAllNodes()) {
                if (!visited.containsKey(n) && visited.keySet().containsAll(n.getParents().keySet())) {
                    // if all parents are already placed, place this node
                    addNode(n);
                }
            }
        }
    }

    private void addNode(Node n) {
        GraphNode graphNode = new GraphNode(n.toString(), n.getCost());
        HBox hbox = new HBox(graphNode);
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);
        visited.put(n, graphNode);
    }

    private void drawLines() {
        boolean right = true;
        for (Map.Entry<Node, GraphNode> node : visited.entrySet()) {
            for (Map.Entry<Node, Integer> child : node.getKey().getChildren().entrySet()) {
                // Draw a cubicCurve!
                GraphNode source = node.getValue();
                GraphNode dest = visited.get(child.getKey());

                // get bounds relative to ParentPane, where we'll be drawing the lines
                Bounds sourceBounds = backgroundPane.sceneToLocal(source.localToScene(source.getBoundsInLocal()));
                Bounds destnBounds = backgroundPane.sceneToLocal(dest.localToScene(dest.getBoundsInLocal()));

                CubicCurve cubicCurve = new CubicCurve();
                //Line cubicCurve = new Line(sourceBounds.getMaxX(), sourceBounds.getMaxY(), destnBounds.getMaxX(), destnBounds.getMaxY());
                cubicCurve.setStrokeWidth(4);
                cubicCurve.setStroke(Color.DIMGRAY);
                cubicCurve.setFill(Color.TRANSPARENT);

                // ugly code binding X start, end, Y start, end
                cubicCurve.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return sourceBounds.getMinX() + sourceBounds.getWidth() / 2;
                }, source.layoutBoundsProperty()));

                cubicCurve.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return sourceBounds.getMinY() + sourceBounds.getHeight() / 2;
                }, source.layoutBoundsProperty()));

                cubicCurve.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return destnBounds.getMinX() + destnBounds.getWidth() / 2;
                }, dest.layoutBoundsProperty()));

                cubicCurve.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return destnBounds.getMinY() + destnBounds.getHeight() / 2;
                }, dest.layoutBoundsProperty()));

                cubicCurve.setControlY1(cubicCurve.getStartY());
                cubicCurve.setControlY2(cubicCurve.getEndY());

                if (right) {
                    cubicCurve.setControlX1(cubicCurve.getStartX() + Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2);
                    cubicCurve.setControlX2(cubicCurve.getEndX() + Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2);
                } else {
                    cubicCurve.setControlX1(cubicCurve.getStartX() - Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2);
                    cubicCurve.setControlX2(cubicCurve.getEndX() - Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2);
                }


                // TODO: Draw weight... somehow!

                backgroundPane.getChildren().add(cubicCurve);
                right = !right;
            }
        }
        sp.setHvalue(0.5);
    }

}
