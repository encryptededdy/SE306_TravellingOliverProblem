package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleNode;


public class GraphNode extends Pane {

    private String name;

    private Circle circle = new Circle();

    private ScheduleNode scheduleNode;

    public GraphNode(String name, Integer cost) {
        super();

        this.name = name;

        circle.setRadius(25);
        circle.setFill(Color.SKYBLUE);

        Text nameLabel = new Text(name);
        Text costLabel = new Text(cost.toString());
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(new Font(20));
        costLabel.setFont(new Font(34));
        costLabel.setOpacity(0.4);
        costLabel.setFill(Color.WHITE);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, costLabel, nameLabel);
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

    public void setScheduleNode(ScheduleNode scheduleNode) {this.scheduleNode = scheduleNode;}

    public void highlight() {
        circle.setFill(Color.ORANGERED);
    }

    public void unHighlight() {
        circle.setFill(Color.SKYBLUE);
    }

    public String getName() {
        return name;
    }
}