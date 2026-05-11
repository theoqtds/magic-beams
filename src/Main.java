import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static final String SEPARATOR   = " ";
    private static final int    ROWS        = 0;
    private static final int    COLUMNS     = 1;
    private static final int NO_CHOSEN_COLUMNS = 0;
    private static final int STARTING_CHOSEN_COLUMN = 1;

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // The first line indicates how many test cases/grids will follow
        int noTests = Integer.parseInt(input.readLine());

        // Process each test case individually
        for (int i = 0; i < noTests; i++) {
            String[] mapDimensions = input.readLine().split(SEPARATOR);
            int noRows = Integer.parseInt(mapDimensions[ROWS]);
            int noColumns = Integer.parseInt(mapDimensions[COLUMNS]);

            String[] chosenColumns = input.readLine().split(SEPARATOR);
            int noChosenColumns = Integer.parseInt(chosenColumns[NO_CHOSEN_COLUMNS]);
            int startingColumn = Integer.parseInt(chosenColumns[STARTING_CHOSEN_COLUMN]);

            int noBeams = Integer.parseInt(input.readLine());
            
            // Initialize a new solver for this specific test case
            BeamSolver pathSolver = new BeamSolver(noRows, noColumns, noChosenColumns, startingColumn, noBeams);

            // Read the grid row by row and add it to the solver's map
            for (int j = 0; j < noBeams; j++) {
                String[] beamChars = input.readLine().split(SEPARATOR);
                int row = Integer.parseInt(beamChars[0]);
                int column = Integer.parseInt(beamChars[1]);
                int length = Integer.parseInt(beamChars[2]);
                char direction = beamChars[3].charAt(0);
                pathSolver.addBeam(row, column, length, direction);
            }

            // Calculate the result and print it to standard output
            System.out.println(pathSolver.answer());
        }
    }
}
