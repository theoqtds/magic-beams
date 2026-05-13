import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

    private static final String SEPARATOR         = " ";
    private static final String RESULT_SEPARATOR  = " ";
    private static final int    ROWS        = 0;
    private static final int    COLUMNS     = 1;
    private static final int NO_CHOSEN_COLUMNS = 0;
    private static final int STARTING_CHOSEN_COLUMN = 1;
    private static final String FALSE_MSG = "False alarm";
    private static final String DISASTER_MSG  = "Disaster";
    private static final String EMPTY  = "";

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        // The first line indicates how many test cases/grids will follow
        int noTests = Integer.parseInt(input.readLine());

        // Process each test case individually
        for (int i = 0; i < noTests; i++) {
            st = new StringTokenizer(input.readLine());
            int noRows = Integer.parseInt(st.nextToken());
            int noColumns = Integer.parseInt(st.nextToken());

            st = new StringTokenizer(input.readLine());
            int noChosenColumns = Integer.parseInt(st.nextToken());
            int startingColumn = Integer.parseInt(st.nextToken());

            int noBeams = Integer.parseInt(input.readLine());
            // Initialize a new solver for this specific test case
            BeamSolver pathSolver = new BeamSolver(noRows, noColumns, noChosenColumns, startingColumn, noBeams);

            // Read the grid row by row and add it to the solver's map
            for (int j = 0; j < noBeams; j++) {
                st = new StringTokenizer(input.readLine());
                int row = Integer.parseInt(st.nextToken());
                int column = Integer.parseInt(st.nextToken());
                int length = Integer.parseInt(st.nextToken());
                char direction = st.nextToken().charAt(0);
                pathSolver.addBeam(row, column, length, direction);
            }

            // Calculate the result and print it to standard output
            Answer answer = pathSolver.answer();
            switch (answer.result()) {
                case BeamSolver.FALSE_ALARM:
                    System.out.println(FALSE_MSG);
                    break;
                case BeamSolver.DISASTER:
                    System.out.println(DISASTER_MSG);
                    break;
                case BeamSolver.FREE:
                    System.out.println(stringifyResult(answer.permutation(), answer.permutationSize()));
                    break;
            }
        }
    }

    private static String stringifyResult(int[] permutation, int permSize) {
        if (permSize == 0)
            return EMPTY;
        StringBuilder sb = new StringBuilder();
        sb.append(permutation[0] + 1);
        for (int i = 1; i < permSize; i++) 
            sb.append(RESULT_SEPARATOR).append(permutation[i] + 1);
        return sb.toString();
    }
}
