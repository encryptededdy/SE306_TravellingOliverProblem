package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.*;

public class GraphDrawer {
    private Pane parentPane;
    private Pane backgroundPane = new Pane();
    private VBox vbox = new VBox();
    private Graph graph;
    private Map<Node, GraphNode> visited = new HashMap<>();

    public GraphDrawer(Pane parentPane, Graph graph) {
        this.parentPane = parentPane;
        this.graph = graph;

        // populate pane
        parentPane.getChildren().add(new StackPane(backgroundPane, vbox));
    }

    public Map<Node, GraphNode> getGraphNodes() {
        return visited;
    }

    public void drawGraph() {
        HashSet<Node> currentLevel = new HashSet<>(graph.getStartingNodes()); // start with... starting nodes
        drawLevel(currentLevel, new HashSet<>());

        // hacky delay
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(event -> drawLines());
        pt.play();
    }

    private void drawLevel(HashSet<Node> currentLevel, HashSet<Node> postponed) {
        HashSet<Node> thisLevel = new HashSet<>();
        HashSet<Node> subLevel = new HashSet<>();
        HBox horizBox = new HBox();
        horizBox.setAlignment(Pos.CENTER);

        Iterator<Node> postponedIterator = postponed.iterator(); // use iterator to prevent concurrentModification when removing from inside loop
        while (postponedIterator.hasNext()) { // consider postponed nodes first
            Node n = postponedIterator.next();
            if (!visited.containsKey(n) &&
                    Collections.disjoint(n.getChildren().keySet(), thisLevel) &&
                    Collections.disjoint(n.getParents().keySet(), thisLevel) &&
                    (thisLevel.size() <= 5)) { // if there are no parents/children on this level
                GraphNode graphNode = new GraphNode(n.toString(), n.getCost());
                horizBox.getChildren().add(graphNode);
                visited.put(n, graphNode);
                subLevel.addAll(n.getChildren().keySet());
                postponedIterator.remove();
                thisLevel.add(n); // but add this to thisLevel
            }
        }
        for (Node n : currentLevel) { // ...then consider the "normal" nodes
            if (thisLevel.size() >= 5) {
                postponed.add(n); // max width of each row is 5
            } else if (!visited.containsKey(n)) {
                if (Collections.disjoint(n.getChildren().keySet(), thisLevel) &&
                        Collections.disjoint(n.getParents().keySet(), thisLevel)) { // if there are no parents/children on this level
                    GraphNode graphNode = new GraphNode(n.toString(), n.getCost());
                    horizBox.getChildren().add(graphNode);
                    visited.put(n, graphNode);
                    thisLevel.add(n);
                    subLevel.addAll(n.getChildren().keySet());
                } else {
                    // Postpone this node
                    postponed.add(n);
                }
            }
        }
        vbox.getChildren().add(horizBox);
        if (subLevel.size() > 0) drawLevel(subLevel, postponed);
    }

    private void drawLines() {
        for (Map.Entry<Node, GraphNode> node : visited.entrySet()) {
            for (Map.Entry<Node, Integer> child : node.getKey().getChildren().entrySet()) {
                // Draw a line!
                GraphNode source = node.getValue();
                GraphNode dest = visited.get(child.getKey());

                // get bounds relative to ParentPane, where we'll be drawing the lines
                Bounds sourceBounds = backgroundPane.sceneToLocal(source.localToScene(source.getBoundsInLocal()));
                Bounds destnBounds = backgroundPane.sceneToLocal(dest.localToScene(dest.getBoundsInLocal()));

                Line line = new Line();
                //Line line = new Line(sourceBounds.getMaxX(), sourceBounds.getMaxY(), destnBounds.getMaxX(), destnBounds.getMaxY());
                line.setStrokeWidth(4);
                line.setStroke(Color.DIMGRAY);

                // ugly code binding X start, end, Y start, end
                line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return sourceBounds.getMinX() + sourceBounds.getWidth()/2;
                }, source.layoutBoundsProperty()));

                line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return sourceBounds.getMinY() + sourceBounds.getHeight()/2;
                }, source.layoutBoundsProperty()));

                line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                    return destnBounds.getMinX() + destnBounds.getWidth()/2;
                }, dest.layoutBoundsProperty()));

                line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                    return destnBounds.getMinY() + destnBounds.getHeight()/2;
                }, dest.layoutBoundsProperty()));


                // TODO: Draw weight... somehow!

                backgroundPane.getChildren().add(line);
            }
        }
    }
}
