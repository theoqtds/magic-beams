package graph;

import java.util.LinkedList;
import java.util.List;

public class Digraph<L> extends AnyGraph<L> {
    private List<Integer>[] predecessorNodes;

    @SuppressWarnings("unchecked")
    public Digraph(int numNodes) {
        super(numNodes);

        predecessorNodes = new List[numNodes];

        for (int i = 0; i < numNodes; i++) {
            predecessorNodes[i] = new LinkedList<>();
        }
    }

    @Override
    public void addEdge(int node1, int node2, L label) {
        successorNodes[node1].add(node2);

        predecessorNodes[node2].add(node1);
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
}