package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import uoa.se306.travellingoliverproblem.visualiser.ColourScheme;
import uoa.se306.travellingoliverproblem.visualiser.schedule.ScheduleNode;


public class GraphNode extends Pane {

    private String name;

    private Circle circle = new Circle();

    private Text costLabel;

    private ScheduleNode scheduleNode;

    private ColourScheme colourScheme;

    public GraphNode(String name, Integer cost, ColourScheme colourScheme) {
        super();

        this.name = name;
        this.colourScheme = colourScheme;

        circle.setRadius(25);


//        Text nameLabel = new Text(name);
        costLabel = new Text(cost.toString());
//        nameLabel.setFill(Color.WHITE);
//        nameLabel.setFont(new Font("Roboto",20));
        costLabel.setFont(new Font("Roboto",25));
        //costLabel.setOpacity(0.4);
        unHighlight();


        StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, costLabel);
        stack.setPadding(new Insets(5));

        circle.setOnMouseEntered(event -> {
            if (scheduleNode != null) {
                scheduleNode.highlight();
            }
            highlight();
        });
//        nameLabel.setOnMouseEntered(event -> {
//            if (scheduleNode != null) {
//                scheduleNode.highlight();
//            }
//            highlight();
//        });
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
//        nameLabel.setOnMouseExited(event -> {
//            if (scheduleNode != null) {
//                scheduleNode.unHighlight();
//            }
//            unHighlight();
//        });
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
        circle.setFill(this.colourScheme.mainAccent);
        circle.setStroke(this.colourScheme.mainAccent);
        costLabel.setFill(this.colourScheme.fgMainAccent);
    }

    public void unHighlight() {
        circle.setFill(this.colourScheme.backgroundColor);
        circle.setStroke(this.colourScheme.lighterAccent);
        costLabel.setFill(this.colourScheme.mainAccent);
    }

    public String getName() {
        return name;
    }
}