package graph;

import java.util.ArrayList;
import java.util.List;

public class Digraph<L> extends AnyGraph<L> {
    public Digraph(int numNodes) {
        super(numNodes);
    }

    @Override
    void addEdge(int node1, int node2, L label) {
        edges.add(new Edge<L>(label, node1, node2));
        nodes[node1].add(node2);
    }

    int inDegree(int node) {
        int degree = 0;
        for (List<Integer> nodeSuccessors : nodes) {
            if (nodeSuccessors != null && nodeSuccessors.contains(node))
                degree++;
        }
        return degree;
    }

    int outDegree(int node) {
        if (nodes[node].isEmpty())
            return 0;
        else
            return nodes[node].size();
    }

    Iterable<Integer> inAdjacentNodes(int node) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < numNodes(); i++) {
            if (nodes[i] != null && nodes[i].contains(node)) {
                result.add(i);
            }
        }
        return result;
    }

    Iterable<Integer> outAdjacentNodes(int node) {
        return nodes[node];
    }

    Iterable<Edge<L>> inIncidentEdges(int node) {
        return edges.stream().filter(edge -> node == edge.secondNode()).toList();
    }

    Iterable<Edge<L>> outIncidentEdges(int node) {
        return edges.stream().filter(edge -> node == edge.firstNode()).toList();
    }
}
