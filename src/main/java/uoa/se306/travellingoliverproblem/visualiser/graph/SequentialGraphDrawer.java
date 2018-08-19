package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.visualiser.ColourScheme;

import java.util.LinkedHashMap;
import java.util.Map;

public class SequentialGraphDrawer {
    private Pane parentPane;
    private Pane backgroundPane = new Pane();
    private HBox parentHBox = new HBox();
    private Graph graph;
    private LinkedHashMap<Node, GraphNode> visited = new LinkedHashMap<>();
    private ScrollPane sp;
    private ColourScheme colourScheme;

    public SequentialGraphDrawer(Pane parentPane, Graph graph, ScrollPane graphScrollPane, ColourScheme colourScheme) {
        this.sp = graphScrollPane;
        this.parentPane = parentPane;
        this.graph = graph;
        this.colourScheme = colourScheme;
        backgroundPane.setMinWidth(1200);
        backgroundPane.setMinHeight(500);
        backgroundPane.setBackground(new Background(new BackgroundFill(colourScheme.backgroundColor, new CornerRadii(0), new Insets(0))));
        StackPane sp = new StackPane(backgroundPane, parentHBox);
        // populate pane
        parentPane.getChildren().add(sp);
        // TODO: Deal with parentHBox width / centering
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
        GraphNode graphNode = new GraphNode(n.toString(), n.getCost(), colourScheme);
        VBox vBox = new VBox(graphNode);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinWidth(100);
        parentHBox.getChildren().add(vBox);
        visited.put(n, graphNode);
    }

    private void drawLines() {
        boolean top = true;
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
                cubicCurve.setStrokeWidth(2);
                cubicCurve.setStroke(Color.web("#f48b94"));
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

                cubicCurve.setControlX1(cubicCurve.getStartX());
                cubicCurve.setControlX2(cubicCurve.getEndX());

                double ctrlY1;
                double ctrlY2;

                boolean adjacent = lineEndX - lineStartX < 120;

                if (adjacent) {
                    ctrlY1 = cubicCurve.getStartY();
                    ctrlY2 = cubicCurve.getEndY();
                } else if (top) {
                    ctrlY1 = cubicCurve.getStartY() - Math.abs(cubicCurve.getEndX() - cubicCurve.getStartX()) / 4;
                    ctrlY2 = cubicCurve.getEndY() - Math.abs(cubicCurve.getEndX() - cubicCurve.getStartX()) / 4;
                    if (ctrlY1 < 0) {
                        ctrlY1 = 5.0;
                    }
                    if (ctrlY2 < 0) {
                        ctrlY2 = 5.0;
                    }
                } else {
                    ctrlY1 = cubicCurve.getStartY() + Math.abs(cubicCurve.getEndX() - cubicCurve.getStartX()) / 4;
                    ctrlY2 = cubicCurve.getEndY() + Math.abs(cubicCurve.getEndX() - cubicCurve.getStartX()) / 4;
                    if (ctrlY1 > 500.0) {
                        ctrlY1 = 245.0;
                    }
                    if (ctrlY2 > 500.0) {
                        ctrlY2 = 245.0;
                    }
                }

                System.out.println("(" + cubicCurve.getStartX() + "," + ctrlY1 + "," + cubicCurve.getEndX() + "," + ctrlY2 + ")");
                cubicCurve.setControlY1(ctrlY1);
                cubicCurve.setControlY2(ctrlY2);
                backgroundPane.getChildren().add(cubicCurve);

                // calculate midpoint x-coordinate on bezier curve
                System.out.println(lineStartY);
                double bezierMidpointY = (0.125 * lineStartY) + ((3 * 0.125) * ctrlY1) + ((3 * 0.125) * ctrlY2) + (0.125 * lineEndY);
                drawArrowHead((lineStartX + lineEndX + 15) / 2, bezierMidpointY);





                // TODO: Draw weight... somehow!

                top = !top;
            }
        }
        sp.setHvalue(0.5);
    }

    private void drawArrowHead(double arrowHeadTipX, double arrowHeadTipY) {
        double theta = 0.0;

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
        arrowHeadShape.setFill(colourScheme.secondaryAccent);
        backgroundPane.getChildren().add(arrowHeadShape);
        arrowHeadShape.toFront();
    }
}
