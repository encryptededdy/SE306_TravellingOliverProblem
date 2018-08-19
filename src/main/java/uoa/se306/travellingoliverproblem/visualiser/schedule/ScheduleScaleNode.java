package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScheduleScaleNode extends Pane {
    public ScheduleScaleNode(Integer number, double width) {
        super();

        Text nameLabel = new Text(number.toString());
        //Text costLabel = new Text(cost.toString());
        nameLabel.setFill(Color.WHITE);
        nameLabel.setFont(new Font(15));

        HBox labelContainer = new HBox(nameLabel);
        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setMinWidth(width);
        labelContainer.setMaxWidth(width);

        // set view
        getChildren().add(labelContainer);
    }

    // empty entry
    public ScheduleScaleNode(double width) {
        super();

        //Text nameLabel = new Text(number.toString());
        //Text costLabel = new Text(cost.toString());
        //nameLabel.setFill(Color.BLACK);
        //nameLabel.setFont(new Font(15));

        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setMinWidth(width);
        labelContainer.setMaxWidth(width);

        // set view
        getChildren().add(labelContainer);
    }
}
