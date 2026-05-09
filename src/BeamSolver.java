import graph.Digraph;

import java.util.Arrays;

public class BeamSolver {
    public static final char NORTH = 'N';
    public static final char EAST = 'E';
    public static final char WEST = 'W';
    public static final char SOUTH = 'S';
    public static final char CONVERSION_CHAR = '0';
    public static final int LABEL = 1;

    private final int noRows;
    private final int noColumns;
    private final int noChosenColumns;
    private final int startingColumn;
    private final int noBeams;

    private Beam[] beams;
    private int beamMap[][];
    private Digraph<Integer> digraph;

    public BeamSolver(int noRows, int noColumns, int noChosenColumns, int startingColumn, int noBeams) {
        this.noRows = noRows;
        this.noColumns = noColumns;
        this.noChosenColumns = noChosenColumns;
        this.startingColumn =  startingColumn;
        this.noBeams = noBeams;
        this.beams = new Beam[noBeams];
        this.beamMap = new int[noRows][noColumns];
        this.digraph = new Digraph<>(noBeams);
    }

    public void addBeam(char[] beam, int j) {
        beams[j] = new Beam(j, convertCharToInt(beam[0]), convertCharToInt(beam[1]), convertCharToInt(beam[2]), beam[3]);
    }

    public int answer() {
        fillBeamMap();
        fillGraph();
        boolean[] mustBeRemoved = findBeamsToRemove();

        return -1;
    }

    //BFS
    private boolean[] findBeamsToRemove() {
        boolean[] mustBeRemoved = new boolean[noBeams];
        return null;
    }

    //Fast conversion from character to integer
    private int convertCharToInt(char ch) {
        return ch - CONVERSION_CHAR;
    }

    private void fillBeamMap() {
        for (int i = 0; i < noRows; i++) {
            for (int j = 0; j < noColumns; j++) {
                beamMap[i][j] = -1;
            }
        }

        for (int i = 0; i < noBeams; i++) {
            Beam beam = beams[i];

            switch (beam.direction()) {
                case NORTH: fillMapNorth(beam); break;
                case EAST: fillMapEast(beam); break;
                case WEST: fillMapWest(beam); break;
                case SOUTH: fillMapSouth(beam); break;
            }
        }
    }

    private void fillMapNorth(Beam beam) {
        for (int i = beam.row(); i > beam.row() - beam.length(); i--) {
            beamMap[i][beam.column()] = beam.id();
        }
    }

    private void fillMapEast(Beam beam) {
        for (int i = beam.column(); i < beam.column() + beam.length(); i++) {
            beamMap[beam.row()][i] = beam.id();
        }
    }

    private void fillMapWest(Beam beam) {
        for (int i = beam.column(); i > beam.column() - beam.length(); i--) {
            beamMap[beam.row()][i] = beam.id();
        }
    }

    private void fillMapSouth(Beam beam) {
        for (int i = beam.row(); i < beam.row() + beam.length(); i++) {
            beamMap[i][beam.column()] = beam.id();
        }
    }

    private void fillGraph() {
        int[] seenBeams = new int[noBeams];
        Arrays.fill(seenBeams, -1); //Inefficient

        for (Beam beam : beams) {
            switch (beam.direction()) {
                case NORTH: navigateMapNorth(beam, seenBeams); break;
                case EAST: navigateMapEast(beam, seenBeams);  break;
                case WEST: navigateMapWest(beam, seenBeams);  break;
                case SOUTH: navigateMapSouth(beam, seenBeams);  break;
            }
        }
    }

    private void navigateMapNorth(Beam beam, int[] seenBeams) {
        for (int i = beam.row() - beam.length(); i >= 0; i--) {
            int blockingBeamId = beamMap[i][beam.column()];
            if (blockingBeamId != -1 && seenBeams[blockingBeamId] != beam.id()) {
                digraph.addEdge(beam.id(), blockingBeamId, LABEL);
                seenBeams[blockingBeamId] = beam.id();
            }
        }
    }

    private void navigateMapEast(Beam beam, int[] seenBeams) {
        for (int i = beam.column() + beam.length(); i < noColumns; i++) {
            int blockingBeamId = beamMap[beam.row()][i];
            if (blockingBeamId != -1 && seenBeams[blockingBeamId] != beam.id()) {
                digraph.addEdge(beam.id(), blockingBeamId, LABEL);
                seenBeams[blockingBeamId] = beam.id();
            }
        }
    }

    private void navigateMapWest(Beam beam,  int[] seenBeams) {
        for (int i = beam.column() - beam.length(); i >= 0; i--) {
            int blockingBeamId = beamMap[beam.row()][i];
            if (blockingBeamId != -1 && seenBeams[blockingBeamId] != beam.id()) {
                digraph.addEdge(beam.id(), blockingBeamId, LABEL);
                seenBeams[blockingBeamId] = beam.id();
            }
        }
    }

    private void navigateMapSouth(Beam beam,  int[] seenBeams) {
        for (int i = beam.row() + beam.length(); i < noRows; i++) {
            int blockingBeamId = beamMap[i][beam.column()];
            if (blockingBeamId != -1 && seenBeams[blockingBeamId] != beam.id()) {
                digraph.addEdge(beam.id(), blockingBeamId, LABEL);
                seenBeams[blockingBeamId] = beam.id();
            }
        }
    }

    private void filterGraph() {

    }
}

