import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MonotonicShortestPath {
    // to store monotonic  dist from s to vertex i
    private Double[] distTo;
    // to store last edge to vertex i in monotonic shortest path from s
    private DirectedEdge[] edgeTo;

    public MonotonicShortestPath(EdgeWeightedDigraph g, int source) {
        distTo = new Double[g.V()];
        edgeTo = new DirectedEdge[g.V()];

        // arrays to store monotonic inc and monotonic dec distance
        Double[] incDistTo = new Double[g.V()];
        Double[] decDistTo = new Double[g.V()];
        Arrays.fill(incDistTo, Double.POSITIVE_INFINITY);
        Arrays.fill(decDistTo, Double.POSITIVE_INFINITY);
        incDistTo[source] = 0.0;
        decDistTo[source] = 0.0;

        // arrays to store incoming edge to vertex in a monotonic inc & dec
        // Shortest path
        DirectedEdge[] incEdgeTo = new DirectedEdge[g.V()];
        DirectedEdge[] decEdgeTo = new DirectedEdge[g.V()];
        Arrays.fill(incEdgeTo, null);
        Arrays.fill(decEdgeTo, null);

        ArrayList<DirectedEdge> ascSortedEdges = new ArrayList<>(g.E());
        for (DirectedEdge e : g.edges()) {
            ascSortedEdges.add(e);
        }

        ascSortedEdges.sort(Comparator.comparingDouble(DirectedEdge::weight));
        for (DirectedEdge e : ascSortedEdges) {
            relaxEdge(e, incDistTo, incEdgeTo, true);
        }

        for (DirectedEdge e : ascSortedEdges.reversed()) {
            relaxEdge(e, decDistTo, decEdgeTo, false);
        }

        for (int i = 0; i < g.V(); i++) {
            if (incDistTo[i] < decDistTo[i]) {
                distTo[i] = incDistTo[i];
                edgeTo[i] = incEdgeTo[i];
            }
            else {
                distTo[i] = decDistTo[i];
                edgeTo[i] = decEdgeTo[i];
            }
        }
    }

    public double getDist(int vertex) {
        return distTo[vertex];
    }

    public Iterable<DirectedEdge> getPath(int vertex) {
        ArrayList<DirectedEdge> edges = new ArrayList<>();
        while (edgeTo[vertex] != null) {
            edges.add(edgeTo[vertex]);
            vertex = edgeTo[vertex].from();
        }
        return edges.reversed();
    }

    private void relaxEdge(DirectedEdge e, Double[] distToArray, DirectedEdge[] edgeToArray,
                           boolean inc) {
        int v = e.from();
        int w = e.to();

        // if previous edge exists then check for monotonic condition
        if (edgeToArray[v] != null) {
            if (inc) {
                // if prev edge has weight greater than current  edge
                // then monotonic condition doesn't hold in strictly increasing
                // path
                if (edgeToArray[v].weight() >= e.weight()) return;
            }
            else {
                if (edgeToArray[v].weight() <= e.weight()) return;
            }
        }

        // relaxing the edge
        if (distToArray[v] + e.weight() < distToArray[w]) {
            distToArray[w] = distToArray[v] + e.weight();
            edgeToArray[w] = e;
        }
    }

    public static void main(String[] args) {
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(4);
        g.addEdge(new DirectedEdge(0, 1, -1));
        g.addEdge(new DirectedEdge(0, 3, -2));
        g.addEdge(new DirectedEdge(1, 2, -3));
        g.addEdge(new DirectedEdge(3, 2, -2));

        MonotonicShortestPath msp = new MonotonicShortestPath(g, 0);
        System.out.println(msp.getDist(2));
        for (DirectedEdge e : msp.getPath(2)) {
            System.out.println(e.toString() + " ");
        }
    }
}