import graph.Digraph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BeamSolver {
    public static final char NORTH = 'N';
    public static final char EAST = 'E';
    public static final char WEST = 'W';
    public static final char SOUTH = 'S';
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

    public void addBeam(int id, int row, int column, int length, char direction) {
        beams[id] = new Beam(id, row, column, length, direction);
    }

    public String answer() {
        fillBeamMap();
        fillGraph();
        boolean[] mustBeRemoved = findBeamsToRemove();

        boolean anyToRemove = false;
        for (boolean b : mustBeRemoved) {
            if (b) {
                anyToRemove = true;
                break;
            }
        }

        if (!anyToRemove) return "False alarm";

        return "";
    }

    //BFS
    private boolean[] findBeamsToRemove() {
        boolean[] found = new boolean[noBeams];

        for (int i = startingColumn; i < startingColumn + noChosenColumns; i++) {
            for (int j = 0; j < noRows; j++) {
                int beamId = beamMap[j][i];
                if (beamId != -1 && !found[beamId]) {
                    bfsExplore(digraph, found, beamId);
                }
            }
        }
        return found;
    }

    private void bfsExplore(Digraph<Integer> graph, boolean[] found, int root) {
        Queue<Integer> waiting = new LinkedList<>();
        waiting.add(root);
        found[root] = true;
        do {
            int node = waiting.remove();
            for (int v : graph.outAdjacentNodes(node))
                if (!found[v]) {
                    found[v] = true;
                    waiting.add(v);
                }
        } while (!waiting.isEmpty());
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
}

