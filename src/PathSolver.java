import java.util.List;

public class PathSolver {
    private int noRows;
    private int noColumns;
    private int noChosenColumns;
    private int startingColumn;
    private int noBeams;

    private Beam[] beams;

    public PathSolver(int noRows, int noColumns, int noChosenColumns, int startingColumn, int noBeams) {
        this.noRows = noRows;
        this.noColumns = noColumns;
        this.noChosenColumns = noChosenColumns;
        this.startingColumn =  startingColumn;
        this.noBeams = noBeams;

        this.beams = new Beam[noBeams];
    }

    public void addBeam(char[] beam, int j) {
        beams[j] = new Beam(convertCharToInt(beam[0]), convertCharToInt(beam[1]), convertCharToInt(beam[2]), beam[3]);
    }

    public int answer() {
        return -1;
    }

    //Fast conversion from character to integer
    private int convertCharToInt(char ch) {
        return ch - '0';
    }
}
