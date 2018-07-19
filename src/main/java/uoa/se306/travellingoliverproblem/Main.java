package uoa.se306.travellingoliverproblem;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide input task graph and number of processors");
        }

        if (args.length >= 2) {
            String filename = args[0];
            try {
                int processors = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of processors");
                System.exit(1);
            }
            
        }
        // TODO: The rest of the CLI options
    }
}
