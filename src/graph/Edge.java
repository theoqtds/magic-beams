package graph;

public record Edge<L>(L label, int firstNode, int secondNode) {

    public int oppositeNode(int node) {
        if (node == firstNode) {
            return secondNode;
        } else {
            return firstNode;
        }
    }
}
