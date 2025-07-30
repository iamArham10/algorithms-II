import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.KruskalMST;

public class BottleNeckMST {
    private KruskalMST kruskalMST;

    public BottleNeckMST(EdgeWeightedGraph g) {
        EdgeWeightedGraph edgeWeightedGraph = new EdgeWeightedGraph(g);
        if (g.V() == 0) {
            throw new IllegalArgumentException("Graph is Empty");
        }

        if (!checkGraphConnection(g)) {
            throw new IllegalArgumentException("Graph is not connected");
        }

        kruskalMST = new KruskalMST(edgeWeightedGraph);
    }

    public Iterable<Edge> getBottleNeckMST() {
        return kruskalMST.edges();
    }

    public double maxWeightEdge() {
        double maxWeight = Double.NEGATIVE_INFINITY;
        for (Edge e : kruskalMST.edges()) {
            maxWeight = Math.max(e.weight(), maxWeight);
        }
        return maxWeight;
    }

    private Boolean checkGraphConnection(EdgeWeightedGraph weightedGraph) {
        Graph unweightedGraph = new Graph(weightedGraph.V());
        for (Edge e : weightedGraph.edges()) {
            int v = e.either();
            int w = e.other(v);
            unweightedGraph.addEdge(v, w);
        }
        BreadthFirstPaths bfsPath = new BreadthFirstPaths(unweightedGraph, 0);
        for (int i = 0; i < unweightedGraph.V(); ++i) {
            if (!bfsPath.hasPathTo(i)) {
                return false;
            }
        }
        return true;
    }
}
