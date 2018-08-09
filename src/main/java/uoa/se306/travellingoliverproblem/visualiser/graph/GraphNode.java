package uoa.se306.travellingoliverproblem.visualiser.graph;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class GraphNode extends Pane {

    private String name;

    private Circle circle = new Circle();

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
        stack.setPadding(new Insets(10));

        // set view
        getChildren().add(stack);

    }

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