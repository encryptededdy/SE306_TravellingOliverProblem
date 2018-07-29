package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScheduleScaleNode extends Pane {
    public ScheduleScaleNode(Integer number, double width) {
        super();

        Rectangle rect = new Rectangle();
        rect.setHeight(30);
        rect.setWidth(width);
        rect.setFill(Color.TRANSPARENT);

        Text nameLabel = new Text(number.toString());
        //Text costLabel = new Text(cost.toString());
        nameLabel.setFill(Color.BLACK);
        nameLabel.setFont(new Font(15));

        Pane labelContainer = new Pane(nameLabel);
        labelContainer.setPadding(new Insets(0, width/2, 0, width/2));

        Line line = new Line(0, 0, 0, 12);

        //costLabel.setFont(new Font(30));
        //costLabel.setOpacity(0.4);
        //costLabel.setFill(Color.WHITE);

        VBox stack = new VBox();
        stack.getChildren().addAll(line, labelContainer);
        // stack.setPadding(new Insets(20));

        // set view
        getChildren().add(stack);
    }
}
