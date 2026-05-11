import graph.Digraph;
import org.w3c.dom.Node;

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
    private final int noBeams;

    private record findRemove(int amountFound, boolean[] found, int[] toRemove) {}

    private Beam[] beams;
    private int beamCount;
    private int beamIdMap[][];
    private Digraph<Integer> digraph;

    public BeamSolver(int noRows, int noColumns, int noChosenColumns, int startingColumn, int noBeams) {
        this.noRows = noRows;
        this.noColumns = noColumns;
        this.noChosenColumns = noChosenColumns;
        this.startingColumn =  startingColumn;
        this.noBeams = noBeams;
        this.beams = new Beam[noBeams];
        this.beamIdMap = new int[noRows][noColumns];
        this.digraph = new Digraph<>(noBeams);
        this.beamCount = 0;
    }

    public void addBeam(int row, int column, int length, char direction) {
        beams[beamCount] = new Beam(beamCount, beamCount+1, row, column, length, direction);
        beamCount++;
    }

    public Answer answer() {
        fillBeamMap();
        fillGraph();
        findRemove findRemoveAnswer = findBeamsToRemove();
        
        if (findRemoveAnswer.amountFound() > 0)
            return removeBeams(findRemoveAnswer);

        return new Answer(FALSE_ALARM, null, 0);
    }
    
    //BFS
    private findRemove findBeamsToRemove() {
        boolean[] found = new boolean[noBeams];
        int[] toRemove  = new int[noBeams];
        int amountFound = 0;
        for (int i = startingColumn; i < startingColumn + noChosenColumns; i++)
            for (int j = 0; j < noRows; j++) {
                int beamId = beamIdMap[j][i];
                if (beamId != EMPTY){
                    int beamIdx = beamId - 1;
                    if (!found[beamIdx]){
                        amountFound += bfsExplore(digraph, found, beamIdx, toRemove, amountFound);
                    }
                }
            }
        return new findRemove(amountFound, found, toRemove);
    }

    private int bfsExplore(Digraph<Integer> graph, boolean[] found, int root, int[] toRemove, int toRemoveCount) {
        int counter = toRemoveCount;
        Queue<Integer> waiting = new ArrayDeque<>();
        waiting.add(root);
        found[root] = true;
        do {
            // waiting contains the idx of the beams
            int node = waiting.remove();
            toRemove[counter++] = node;
            for (int v : graph.inAdjacentNodes(node))
                if (!found[v]) {
                    found[v] = true;
                    waiting.add(v);
                }
        } while (!waiting.isEmpty());
        return counter - toRemoveCount;
    }

    //Kahn's
    private Answer removeBeams(findRemove info){
        int permSize            = 0;

        int[] inDegree          = new int[digraph.numNodes()];
        int[] permutation       = new int[digraph.numNodes()];
        Queue<Integer> ready    = new PriorityQueue<>();

        int[] toRemove          = info.toRemove();
        int beamsRemoved        = info.amountFound();
        boolean[] mustBeRemoved = info.found();

        // now we dont know which node we have to addres
        for (int i = 0; i < beamsRemoved; i++) {
            int remove = toRemove[i];
            inDegree[remove] = digraph.inDegree(remove);
            if (inDegree[remove] == 0) 
                ready.add(remove);
        }

        while (!ready.isEmpty()) {
            int node = ready.remove();
            permutation[permSize++] = node;
            for (int v : digraph.outAdjacentNodes(node)) {
                if (mustBeRemoved[v]) {
                    inDegree[v]--;
                    if (inDegree[v] == 0) 
                        ready.add(v);
                }
            }
        }
        //System.out.println();
        //System.out.printf("mustBeRemoved: ");
        //for (boolean i : mustBeRemoved) {
        //    System.out.printf(" " + i);
        //}
        //System.out.println();
        //System.out.printf("permutation: ");
        //for (int i : permutation) {
        //    System.out.printf(" "+i);
        //}
        //System.out.println();
        //System.out.println("beamsRemoved: "+beamsRemoved);
        //System.out.println("permSize: "+permSize);
        if (permSize < beamsRemoved) return new Answer(DISASTER, null, 0);
        //i dont understand why we need this
        //if (permSize == 0) return "";
        return new Answer(FREE, permutation, permSize);
    }

    private void fillBeamMap() {
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

    private void fillGraph() {
        // TODO: why have this, we can just skip the 
        // whole beam if they go in the same direction. 
        // if they dont go in the same direction, we wont have
        // clashing problems 
        // (beams are lines, cannot bend:
        //      lines either touche 0, 1 or all points)
        int[] seenBeamsId = new int[noBeams];
//Arrays.fill(seenBeamsId, EMPTY);
        for (int i = 0; i < noBeams; i++) {
            Beam beam = beams[i];
            switch (beam.direction()) {
                case NORTH: navigateMapNorth(beam, seenBeamsId); break;
                case EAST: navigateMapEast(beam, seenBeamsId);  break;
                case WEST: navigateMapWest(beam, seenBeamsId);  break;
                case SOUTH: navigateMapSouth(beam, seenBeamsId);  break;
            }
        }
    }

    // NAVIGATE MAPS
    private void navigateMapNorth(Beam beam, int[] seenBeamsId) {
        int beamExit = beam.row() - beam.length();
        int blockingBeamIdx, blockingBeamId;
        for (int i = beamExit; i >= 0; i--) {
            blockingBeamId = beamIdMap[i][beam.column()];
            if (blockingBeamId != EMPTY ) {
                blockingBeamIdx = blockingBeamId - 1;
                if (seenBeamsId[blockingBeamIdx] != beam.id()){
                    digraph.addEdge(blockingBeamIdx, beam.idx(), LABEL);
                    seenBeamsId[blockingBeamIdx] = beam.id();
                }
            }
        }
    }

    private void navigateMapEast(Beam beam, int[] seenBeamsId) {
        int beamExit = beam.column() + beam.length(); 
        int blockingBeamIdx, blockingBeamId;
        for (int i = beamExit ; i < noColumns; i++) {
            blockingBeamId = beamIdMap[beam.row()][i];
            if (blockingBeamId != EMPTY ) {
                blockingBeamIdx = blockingBeamId - 1;
                if (seenBeamsId[blockingBeamIdx] != beam.id()){
                    digraph.addEdge(blockingBeamIdx, beam.idx(), LABEL);
                    seenBeamsId[blockingBeamIdx] = beam.id();
                }
            }
        }
    }

    private void navigateMapWest(Beam beam,  int[] seenBeamsId) {
        int beamExit = beam.column() - beam.length();
        int blockingBeamIdx, blockingBeamId;
        for (int i = beamExit ; i >= 0; i--) {
            blockingBeamId = beamIdMap[beam.row()][i];
            if (blockingBeamId != EMPTY ) {
                blockingBeamIdx = blockingBeamId - 1;
                if (seenBeamsId[blockingBeamIdx] != beam.id()){
                    digraph.addEdge(blockingBeamIdx, beam.idx(), LABEL);
                    seenBeamsId[blockingBeamIdx] = beam.id();
                }
            }
        }
    }

    private void navigateMapSouth(Beam beam,  int[] seenBeamsId) {
        int beamExit = beam.row() + beam.length();
        int blockingBeamIdx, blockingBeamId;
        for (int i = beamExit; i < noRows; i++) {
            blockingBeamId = beamIdMap[i][beam.column()];
            if (blockingBeamId != EMPTY ) {
                blockingBeamIdx = blockingBeamId - 1;
                if (seenBeamsId[blockingBeamIdx] != beam.id()){
                    digraph.addEdge(blockingBeamIdx, beam.idx(), LABEL);
                    seenBeamsId[blockingBeamIdx] = beam.id();
                }
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
}

