package graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AnyGraph<L> {
    protected List<Integer>[] successorNodes;

    @SuppressWarnings("unchecked")
    public AnyGraph(int numNodes) {
        successorNodes = new List[numNodes];

        for (int i = 0; i < numNodes; i++) {
            successorNodes[i] = new LinkedList<>();
        }
    }

    public Iterable<Integer> nodes() {
        return () -> new Iterator<Integer>() {
            int current = 0;
            @Override
            public boolean hasNext() {
                return current < numNodes();
            }

            @Override
            public Integer next() {
                return current++;
            }
        };
    }

    public int numNodes() {
        return successorNodes.length;
    }

    public int aNode() {
        if (numNodes() > 0) return 0;
        return -1;
    }

    public abstract void addEdge(int node1, int node2, L label);

    public boolean edgeExists(int node1, int node2) {
        return successorNodes[node1].contains(node2);
    }
}
