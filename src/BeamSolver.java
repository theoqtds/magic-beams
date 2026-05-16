import graph.Digraph;

import java.util.*;


public class BeamSolver {
    public static final char NORTH = 'N';
    public static final char EAST = 'E';
    public static final char WEST = 'W';
    public static final char SOUTH = 'S';
    public static final int LABEL = 1;
    public static final int EMPTY = 0;
    public static final int DISASTER    = 0;
    public static final int FALSE_ALARM = 1;
    public static final int FREE        = 2;

    private final int noRows;
    private final int noColumns;
    private final int noChosenColumns;
    private final int startingColumn;
    private final int beamsSize;

    private Beam[] beams;
    private int beamCount;
    private int beamIdMap[][];
    private Digraph<Integer> digraph;

    public BeamSolver(int noRows, int noColumns, int noChosenColumns, int startingColumn, int noBeams) {
        this.noRows = noRows;
        this.noColumns = noColumns;
        this.noChosenColumns = noChosenColumns;
        this.startingColumn =  startingColumn;
        this.beamsSize = noBeams + 1;
        // idx 0 saved for the empty spaces
        this.beams = new Beam[beamsSize];
        this.beamIdMap = new int[noRows][noColumns];
        this.digraph = new Digraph<>(beamsSize);
        this.beamCount = 1;
    }

    public void addBeam(int row, int column, int length, char direction) {
        beams[beamCount] = new Beam(beamCount, row, column, length, direction);
        beamCount++;
    }

    public Answer answer() {
        fillBeamMap();
        fillGraph();
        FindRemove findRemoveAnswer = findBeamsToRemove();
        
        if (findRemoveAnswer.amountFound() > 0)
            return removeBeams(findRemoveAnswer);

        return new Answer(FALSE_ALARM, null, 0);
    }
    
    //BFS
    private FindRemove findBeamsToRemove() {
        boolean[] found = new boolean[beamsSize];
        int[] toRemove  = new int[beamsSize];
        int amountFound = 0;
        for (int i = startingColumn; i < startingColumn + noChosenColumns; i++)
            for (int j = 0; j < noRows; j++) {
                int beamId = beamIdMap[j][i];
                if (beamId != EMPTY && !found[beamId])
                    amountFound = bfsExplore(digraph, found, beamId, toRemove, amountFound);
            }
        return new FindRemove(amountFound, found, toRemove);
    }

    // returns the next index jumped "toRemoveCount" jumps to
    private int bfsExplore(Digraph<Integer> graph, boolean[] found, int root, int[] toRemove, int toRemoveCount) {
        int count = toRemoveCount;
        Queue<Integer> waiting = new ArrayDeque<>();
        waiting.add(root);
        found[root] = true;
        do {
            int node = waiting.remove();
            toRemove[count++] = node;
            for (int v : graph.inAdjacentNodes(node))
                if (!found[v]) {
                    found[v] = true;
                    waiting.add(v);
                }
        } while (!waiting.isEmpty());
        return count;
    }

    //Kahn's
    private Answer removeBeams(FindRemove info){
        int permSize            = 0;
        int[] inDegree          = new int[digraph.numNodes()];
        int[] permutation       = new int[digraph.numNodes()];
        Queue<Integer> ready    = new PriorityQueue<>();
        int[] toRemove          = info.toRemove();
        int beamsRemoved        = info.amountFound();
        boolean[] mustBeRemoved = info.found();

        for (int i = 0; i < beamsRemoved; i++) {
            int remove = toRemove[i];
            inDegree[remove] = digraph.inDegree(remove);
            if (inDegree[remove] == 0) 
                ready.add(remove);
        }

        while (!ready.isEmpty()) {
            int node = ready.remove();
            // return the actual index of the ray
            permutation[permSize++] = node-1;
            for (int v : digraph.outAdjacentNodes(node))
                if (mustBeRemoved[v]) {
                    inDegree[v]--;
                    if (inDegree[v] == 0) 
                        ready.add(v);
                }
        }
        if (permSize < beamsRemoved) return new Answer(DISASTER, null, 0);
        return new Answer(FREE, permutation, permSize);
    }

    private void fillBeamMap() {
        for (int i = 1; i < beamsSize; i++) {
            Beam beam = beams[i];
            switch (beam.direction()) {
                case NORTH: fillMapNorth(beam); break;
                case EAST: fillMapEast(beam); break;
                case WEST: fillMapWest(beam); break;
                case SOUTH: fillMapSouth(beam); break;
            }
        }
    }

    // FILL MAPS    
    private void fillMapNorth(Beam beam) {
        int lastFill = beam.row() - beam.length();
        for (int i = beam.row(); i > lastFill; i--) {
            beamIdMap[i][beam.column()] = beam.id();
        }
    }

    private void fillMapEast(Beam beam) {
        int lastFill = beam.column() + beam.length();
        for (int i = beam.column(); i < lastFill; i++) {
            beamIdMap[beam.row()][i] = beam.id();
        }
    }

    private void fillMapWest(Beam beam) {
        int lastFill = beam.column() - beam.length();
        for (int i = beam.column(); i > lastFill; i--) {
            beamIdMap[beam.row()][i] = beam.id();
        }
    }

    private void fillMapSouth(Beam beam) {
        int lastFill = beam.row() + beam.length();
        for (int i = beam.row(); i < lastFill; i++) {
            beamIdMap[i][beam.column()] = beam.id();
        }
    }
    private void fillGraph() {
        for (int i = 1; i < beamsSize; i++) {
            Beam beam = beams[i];
            switch (beam.direction()) {
                case NORTH: navigateMapNorth(beam); break;
                case EAST: navigateMapEast(beam);  break;
                case WEST: navigateMapWest(beam);  break;
                case SOUTH: navigateMapSouth(beam);  break;
            }
        }
    }

    // NAVIGATE MAPS
    private boolean checkSameAxis(Beam tmp, Beam beam){
        return switch (beam.direction()) {
            case NORTH, SOUTH -> tmp.direction() == NORTH || tmp.direction() == SOUTH;
            case WEST, EAST   -> tmp.direction() == WEST || tmp.direction() == EAST;
            default -> false;
        };
    }


    // checks whether the blockingBeamId is actually blocking the beam
    // returns how many "houses to skip" if it is actually blocking
    private int processBlockedBeam(int blockingBeamId, Beam beam){
            if (blockingBeamId != EMPTY ) {
                digraph.addEdge(blockingBeamId, beam.id(), LABEL);
                Beam tmp = beams[blockingBeamId];
                if (checkSameAxis(tmp, beam))
                    return tmp.length() - 1;
            }
        return 0;
    }

    private void navigateMapNorth(Beam beam) {
        int beamExit = beam.row() - beam.length();
        int blockingBeamId;
        for (int i = beamExit; i >= 0; i--) {
            blockingBeamId = beamIdMap[i][beam.column()];
            // skips length - 1 if both beams are in the same direction or 0 if not
            i -= processBlockedBeam(blockingBeamId, beam);
        }
    }

    private void navigateMapEast(Beam beam) {
        int beamExit = beam.column() + beam.length(); 
        int blockingBeamId;
        for (int i = beamExit ; i < noColumns; i++) {
            blockingBeamId = beamIdMap[beam.row()][i];
            i += processBlockedBeam(blockingBeamId, beam);
        }
    }

    private void navigateMapWest(Beam beam) {
        int beamExit = beam.column() - beam.length();
        int blockingBeamId;
        for (int i = beamExit ; i >= 0; i--) {
            blockingBeamId = beamIdMap[beam.row()][i];
            i -= processBlockedBeam(blockingBeamId, beam);
        }
    }

    private void navigateMapSouth(Beam beam) {
        int beamExit = beam.row() + beam.length();
        int blockingBeamId;
        for (int i = beamExit; i < noRows; i++) {
            blockingBeamId = beamIdMap[i][beam.column()];
            i += processBlockedBeam(blockingBeamId, beam);
        }
    }
}

