package uoa.se306.travellingoliverproblem.visualiser;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ColourScheme {
    public final Color backgroundColor;
    public final Color mainAccent;
    public final Color lighterAccent;
    public final Color fgMainAccent;
    public final Color secondaryAccent;
    public final Color lighterSecondaryAccent;
    public final Color fgSecondaryAccent;

    public ColourScheme(String backgroundColor, String mainAccent, String lighterAccent, String fgMainAccent, String secondaryAccent, String lighterSecondaryAccent, String fgSecondaryAccent) {
        this.backgroundColor = Color.web(backgroundColor);
        this.mainAccent = Color.web(mainAccent);
        this.lighterAccent = Color.web(lighterAccent);
        this.fgMainAccent = Color.web(fgMainAccent);
        this.secondaryAccent = Color.web(secondaryAccent);
        this.lighterSecondaryAccent = Color.web(lighterSecondaryAccent);
        this.fgSecondaryAccent = Color.web(fgSecondaryAccent);
    }
}
