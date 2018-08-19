package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleNode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class GraphNode extends Pane {

    private String name;

    private Circle circle = new Circle();

    private Color circleColor = Color.SKYBLUE;

    private ScheduleNode scheduleNode;

    private HashMap<Shape, Polygon> parentEdges;

    private HashMap<Shape, Polygon> childEdges;

    private Integer cost;

    public GraphNode(String name, Integer cost, Integer inNodes, Integer outNodes) {
        super();

        this.cost = cost;
        this.name = name;
        this.parentEdges = new HashMap<>();
        this.childEdges = new HashMap<>();

        circle.setRadius(25);
        circle.setFill(circleColor);
        circle.setStroke(Color.WHITE);

        Tooltip t = new Tooltip(String.format("Cost: %s, In-nodes: %s, Out-nodes: %s", cost, inNodes, outNodes));
        hackTooltipStartTiming(t);
        Tooltip.install(this, t);

        Text nameLabel = new Text(name);
        Text costLabel = new Text(cost.toString());
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(new Font("Roboto",20));
        //costLabel.setFont(new Font("Roboto",28));
        //costLabel.setOpacity(0.4);
        //costLabel.setFill(Color.WHITE);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, nameLabel);
        stack.setPadding(new Insets(5));

        circle.setOnMouseEntered(event -> {
            if (scheduleNode != null) {
                scheduleNode.highlight();
            }
            highlight();
        });
        nameLabel.setOnMouseEntered(event -> {
            if (scheduleNode != null) {
                scheduleNode.highlight();
            }
            highlight();
        });
        costLabel.setOnMouseEntered(event -> {
            if (scheduleNode != null) {
                scheduleNode.highlight();
            }
            highlight();
        });

        circle.setOnMouseExited(event -> {
            if (scheduleNode != null) {
                scheduleNode.unHighlight();
            }
            unHighlight();
        });
        nameLabel.setOnMouseExited(event -> {
            if (scheduleNode != null) {
                scheduleNode.unHighlight();
            }
            unHighlight();
        });
        costLabel.setOnMouseExited(event -> {
            if (scheduleNode != null) {
                scheduleNode.unHighlight();
            }
            unHighlight();
        });

        // set view
        getChildren().add(stack);

    }
    public void addParentEdge(Shape lineEdge, Polygon arrowHeadShape) {
        this.parentEdges.put(lineEdge, arrowHeadShape);
    }

    public void addChildEdge(Shape lineEdge, Polygon arrowHeadShape) {
        this.childEdges.put(lineEdge, arrowHeadShape);
    }

    public void setScheduleNode(ScheduleNode scheduleNode) {this.scheduleNode = scheduleNode;}

    public void setCircleColour(Color color) {
        circle.setFill(color);
        circleColor = color;
    }

    public void highlight() {
        circle.setFill(Color.ORANGERED);

        for (Map.Entry<Shape, Polygon> edge : this.parentEdges.entrySet()) {
            edge.getKey().setStroke(Color.WHITE);
            edge.getValue().setFill(Color.WHITE);
        }

        for (Map.Entry<Shape, Polygon> edge : this.childEdges.entrySet()) {
            edge.getKey().setStroke(Color.YELLOW);
            edge.getValue().setFill(Color.YELLOW);
        }
    }

    public void unHighlight() {
        circle.setFill(circleColor);

        for (Map.Entry<Shape, Polygon> edge : this.parentEdges.entrySet()) {
            edge.getKey().setStroke(Color.DIMGRAY);
            edge.getValue().setFill(Color.GRAY);
        }

        for (Map.Entry<Shape, Polygon> edge : this.childEdges.entrySet()) {
            edge.getKey().setStroke(Color.DIMGRAY);
            edge.getValue().setFill(Color.GRAY);
        }
    }

    public String getName() {
        return name;
    }

    // Reduce the tooltip delay
    // Source: https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
    public void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(50)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}