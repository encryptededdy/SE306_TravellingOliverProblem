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
import javafx.scene.shape.Polygon;
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

                double lineEndX = destnBounds.getMinX() + destnBounds.getWidth()/2;
                double lineEndY = destnBounds.getMinY() + destnBounds.getHeight()/2;

                double lineStartX = sourceBounds.getMinX() + sourceBounds.getWidth()/2;
                double lineStartY = sourceBounds.getMinY() + sourceBounds.getHeight()/2;

                // ugly code binding X start, end, Y start, end
                cubicCurve.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return lineStartX;
                }, source.layoutBoundsProperty()));

                cubicCurve.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return lineStartY;
                }, source.layoutBoundsProperty()));

                cubicCurve.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return lineEndX;
                }, dest.layoutBoundsProperty()));

                cubicCurve.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return lineEndY;
                }, dest.layoutBoundsProperty()));

                cubicCurve.setControlY1(cubicCurve.getStartY());
                cubicCurve.setControlY2(cubicCurve.getEndY());

                double ctrlX1;
                double ctrlX2;

                boolean adjacent = lineEndY - lineStartY < 100 ? true : false;

                if (adjacent) {
                    ctrlX1 = cubicCurve.getStartX();
                    ctrlX2 = cubicCurve.getEndX();
                } else if (right) {
                    ctrlX1 = cubicCurve.getStartX() + Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2;
                    ctrlX2 = cubicCurve.getEndX() + Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2;
                } else {
                    ctrlX1 = cubicCurve.getStartX() - Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2;
                    ctrlX2 = cubicCurve.getEndX() - Math.abs(cubicCurve.getEndY() - cubicCurve.getStartY()) / 2;
                }
                cubicCurve.setControlX1(ctrlX1);
                cubicCurve.setControlX2(ctrlX2);
                backgroundPane.getChildren().add(cubicCurve);

                // calculate midpoint x-coordinate on bezier curve
                double bezierMidpointX = (0.125 * lineStartX) + ((3 * 0.125) * ctrlX1) + ((3 * 0.125) * ctrlX2) + (0.125 * lineEndX);
                drawArrowHead(bezierMidpointX, (lineStartY + lineEndY + 15) / 2);





                // TODO: Draw weight... somehow!

                right = !right;
            }
        }
        sp.setHvalue(0.5);
    }

    private void drawArrowHead(double arrowHeadTipX, double arrowHeadTipY) {
        double theta = Math.PI / 2;

        double arrowMidPointX =  arrowHeadTipX + (-15 * Math.cos(theta));
        double arrowMidPointY =  arrowHeadTipY + (-15 * Math.sin(theta));

        double beta = (Math.PI/2) - theta;
        double leftDeltaX = 7 * Math.cos(beta);
        double leftDeltaY = 7 * Math.sin(beta);

        double arrowHeadLeftPointX = arrowMidPointX + leftDeltaX;
        double arrowHeadLeftPointY = arrowMidPointY - leftDeltaY;

        double arrowHeadRightPointX = arrowMidPointX - leftDeltaX;
        double arrowHeadRightPointY = arrowMidPointY + leftDeltaY;

        Polygon arrowHeadShape = new Polygon();
        arrowHeadShape.getPoints().addAll(new Double[]{
                arrowHeadTipX, arrowHeadTipY,
                arrowHeadLeftPointX, arrowHeadLeftPointY,
                arrowHeadRightPointX, arrowHeadRightPointY
        });
        arrowHeadShape.setFill(Color.BLACK);

        backgroundPane.getChildren().add(arrowHeadShape);
    }
}
