package graph;

import java.util.LinkedList;
import java.util.List;

public class Digraph<L> extends AnyGraph<L> {
    private List<Integer>[] predecessorNodes;
    private List<Edge<L>>[] incomingEdges;
    private List<Edge<L>>[] outgoingEdges;

    @SuppressWarnings("unchecked")
    public Digraph(int numNodes) {
        super(numNodes);

        predecessorNodes = new List[numNodes];
        incomingEdges = new List[numNodes];
        outgoingEdges = new List[numNodes];

        for (int i = 0; i < numNodes; i++) {
            predecessorNodes[i] = new LinkedList<>();
            incomingEdges[i] = new LinkedList<>();
            outgoingEdges[i] = new LinkedList<>();
        }
    }

    @Override
    public void addEdge(int node1, int node2, L label) {
        Edge<L> newEdge = new Edge<L>(label, node1, node2);
        edges.add(newEdge);

        successorNodes[node1].add(node2);
        outgoingEdges[node1].add(newEdge);

        predecessorNodes[node2].add(node1);
        incomingEdges[node2].add(newEdge);

    }

    public int inDegree(int node) {
        return predecessorNodes[node].size();
    }

    public int outDegree(int node) {
        return successorNodes[node].size();
    }

    public Iterable<Integer> inAdjacentNodes(int node) {
        return predecessorNodes[node];
    }

    public Iterable<Integer> outAdjacentNodes(int node) {
        return successorNodes[node];
    }

    public Iterable<Edge<L>> inIncidentEdges(int node) {
        return incomingEdges[node];
    }

    public Iterable<Edge<L>> outIncidentEdges(int node) {
        return outgoingEdges[node];
    }
}