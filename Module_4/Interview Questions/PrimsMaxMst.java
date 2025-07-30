import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

public class PrimsMaxMst {
    private IndexedMaxPQ<Edge> maxPQ;
    private Bag<Edge> spanningTreeEdges;
    private boolean[] marked;

    PrimsMaxMst(EdgeWeightedGraph G) {
        maxPQ = new IndexedMaxPQ<>(G.V());
        spanningTreeEdges = new Bag<>();
        marked = new boolean[G.V()];
        for (int i = 0; i < G.V(); i++) {
            marked[i] = false;
        }

        for (int i = 0; i < G.V(); i++) {
            if (!marked[i]) {
                runPrims(G, i);
            }
        }
    }

    public Iterable<Edge> edges() {
        return spanningTreeEdges;
    }

    public double weight() {
        double weight = 0.0;
        for (Edge e : spanningTreeEdges) {
            weight += e.weight();
        }
        return weight;
    }

    private void runPrims(EdgeWeightedGraph G, int s) {
        marked[s] = true;
        for (Edge e : G.adj(s)) {
            maxPQ.insert(e.other(s), e);
        }
        while (!maxPQ.isEmpty()) {
            Edge e = maxPQ.maxKey();
            int v = maxPQ.delMax();
            if (!marked[v]) {
                spanningTreeEdges.add(e);
                scan(G, v);
            }
        }
    }

    private void scan(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (!marked[w]) {
                // check if there is a w in the graph
                if (maxPQ.contains(w)) {
                    if (e.weight() > maxPQ.keyOf(w).weight()) {
                        maxPQ.increaseKey(w, e);
                    }
                }
                else {
                    maxPQ.insert(w, e);
                }
            }
        }
    }
}
