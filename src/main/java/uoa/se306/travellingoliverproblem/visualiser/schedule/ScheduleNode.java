package uoa.se306.travellingoliverproblem.visualiser.schedule;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.visualiser.graph.GraphNode;

import java.lang.reflect.Field;

public class ScheduleNode extends Pane {
    private String name;
    private Rectangle rect;

    // normal schedule
    public ScheduleNode(double width, ScheduleEntry schedule, GraphNode graphNode) {
        super();

        this.name = schedule.getNode().toString();

        rect = new Rectangle();
        rect.setHeight(ScheduleDrawer.ROW_HEIGHT);
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

        graphNode.setScheduleNode(this);

        this.setOnMouseEntered(event -> {
            graphNode.highlight();
            highlight();
        });

        this.setOnMouseExited(event -> {
            graphNode.unHighlight();
            unHighlight();
        });

        Tooltip t = new Tooltip(String.format("Start time: %s, End time: %s, Cost: %s", schedule.getStartTime(), schedule.getEndTime(), schedule.getLength()));
        hackTooltipStartTiming(t);
        Tooltip.install(this, t);

        // set view
        getChildren().add(stack);
    }

    // processor header
    public ScheduleNode(String processorName) {
        super();

        this.name = processorName;

        Rectangle rect = new Rectangle();
        rect.setHeight(ScheduleDrawer.ROW_HEIGHT);
        rect.setWidth(ScheduleDrawer.HEADER_WIDTH);
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
    public ScheduleNode(double space) {
        super();

        this.name = "Empty space";

        Rectangle rect = new Rectangle();
        rect.setHeight(ScheduleDrawer.ROW_HEIGHT);
        rect.setWidth(space);
        rect.setFill(Color.TRANSPARENT);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect);

        // set view
        getChildren().add(stack);
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



    public void highlight() { rect.setFill(Color.ORANGERED); }

    public void unHighlight() { rect.setFill(Color.SKYBLUE); }


    public String getName() {
        return name;
    }
}
