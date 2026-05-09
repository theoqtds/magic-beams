package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AnyGraph<L> {
    protected List<Integer>[] nodes;
    protected List<Edge<L>> edges;

    @SuppressWarnings("unchecked")
    public AnyGraph(int numNodes) {
        nodes = new List[numNodes];

        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new LinkedList<>();
        }

        edges = new ArrayList<>();
    }

    public Iterable<Integer> nodes() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < numNodes(); i++) {
            result.add(i);
        }
        return result;
    }

    public Iterable<Edge<L>> edges() {
        return edges;
    }

    public int numNodes() {
        return nodes.length;
    }

    public int numEdges() {
        return edges.size();
    }

    public int aNode() {
        if (numNodes() > 0) return 0;
        return -1;
    }

    public void addEdge(int node1, int node2, L label) {

    }

    public boolean edgeExists(int node1, int node2) {
        return nodes[node1].contains(node2);
    }
}
