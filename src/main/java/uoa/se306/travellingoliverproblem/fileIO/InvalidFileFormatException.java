package uoa.se306.travellingoliverproblem.fileIO;

public class InvalidFileFormatException extends RuntimeException {
    public InvalidFileFormatException (Integer lineNo, String line) {
        super("Error reading file at line number "+lineNo+" ("+line+")");
    }

    public InvalidFileFormatException (String error) {
        super(error);
    }
}
