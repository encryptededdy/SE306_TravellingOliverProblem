package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScheduleNode extends Pane {
    private String name;

    // normal schedule
    public ScheduleNode(String name, Integer cost, int width) {
        super();

        this.name = name;

        Rectangle rect = new Rectangle();
        rect.setHeight(30);
        rect.setWidth(width);
        rect.setFill(Color.SKYBLUE);
        rect.setStroke(Color.BLACK);

        Text nameLabel = new Text(name);
        //Text costLabel = new Text(cost.toString());
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(new Font(15));
        //costLabel.setFont(new Font(30));
        //costLabel.setOpacity(0.4);
        //costLabel.setFill(Color.WHITE);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, nameLabel);
        // stack.setPadding(new Insets(20));

        // set view
        getChildren().add(stack);
    }

    // processor header
    public ScheduleNode(String processorName) {
        super();

        this.name = processorName;

        Rectangle rect = new Rectangle();
        rect.setHeight(30);
        rect.setWidth(100);
        rect.setFill(Color.TRANSPARENT);

        Text nameLabel = new Text(name);
        nameLabel.setFill(Color.BLACK);
        nameLabel.setFont(new Font(15));

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, nameLabel);

        // set view
        getChildren().add(stack);
    }

    // empty space
    public ScheduleNode(int space) {
        super();

        this.name = "Empty space";

        Rectangle rect = new Rectangle();
        rect.setHeight(30);
        rect.setWidth(space);
        rect.setFill(Color.TRANSPARENT);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect);

        // set view
        getChildren().add(stack);
    }


    public String getName() {
        return name;
    }
}
