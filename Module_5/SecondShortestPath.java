import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecondShortestPath {
    private double secondShortestDist;
    private List<DirectedEdge> secondShortestPath;

    public SecondShortestPath(EdgeWeightedDigraph g, int source, int destination) {
        // shortest path from s to all other vertices
        DijkstraSP spFromS = new DijkstraSP(g, source);

        // shortest path from all other vertices to destination
        EdgeWeightedDigraph reversedG = reverseGraph(g);
        DijkstraSP spFromD = new DijkstraSP(reversedG, destination);

        int splitVertex = findPathVertex(g, spFromS, spFromD, destination);

        if (splitVertex == -1) {
            secondShortestDist = Double.POSITIVE_INFINITY;
            secondShortestPath = Collections.emptyList();
        }
        else {
            secondShortestDist = spFromS.distTo(splitVertex) + spFromD.distTo(splitVertex);
            secondShortestPath = buildPath(spFromS, spFromD, splitVertex);
        }
    }

    public double getPathDist() {
        return secondShortestDist;
    }

    public Iterable<DirectedEdge> getPathEdges() {
        return secondShortestPath;
    }

    // finds the path vertex such that path from source to path vertex and path
    // from path vertex to destination gives the second shortest path
    private int findPathVertex(EdgeWeightedDigraph g, DijkstraSP spFromS,
                               DijkstraSP spFromD, int destination) {
        double pathDist = Double.POSITIVE_INFINITY;
        int pathVertex = -1;
        for (int v = 0; v < g.V(); v++) {
            if (spFromS.hasPathTo(v) && spFromD.hasPathTo(v)) {
                double dist = spFromS.distTo(v) + spFromD.distTo(v);
                if (dist < pathDist && dist > spFromS.distTo(destination)) {
                    pathVertex = v;
                    pathDist = dist;
                }
            }
        }
        return pathVertex;
    }

    // Builds the second-shortest path from source to split vertex and from
    // split vertex to destination
    private List<DirectedEdge> buildPath(DijkstraSP spFromS, DijkstraSP spFromD, int splitVertex) {
        List<DirectedEdge> path = new ArrayList<>();

        // add edges from source to splitVertex
        for (DirectedEdge e : spFromS.pathTo(splitVertex)) {
            path.add(e);
        }

        // add edges from splitVertex  destination
        Stack<DirectedEdge> stack = new Stack<>();
        for (DirectedEdge e : spFromD.pathTo(splitVertex)) {
            // spFromD is run on reversed graph, so flip edges back
            stack.push(new DirectedEdge(e.to(), e.from(), e.weight()));
        }
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }

        return path;
    }

    // Reverses the graph by changing the direction of each edge
    private EdgeWeightedDigraph reverseGraph(EdgeWeightedDigraph g) {
        EdgeWeightedDigraph reversedGraph = new EdgeWeightedDigraph(g.V());
        for (DirectedEdge e : g.edges()) {
            reversedGraph.addEdge(new DirectedEdge(e.to(), e.from(), e.weight()));
        }
        return reversedGraph;
    }

    public static void main(String[] args) {
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(4);
        g.addEdge(new DirectedEdge(0, 1, 1));
        g.addEdge(new DirectedEdge(0, 3, 1));
        g.addEdge(new DirectedEdge(3, 2, 2));
        g.addEdge(new DirectedEdge(1, 2, 1));

        SecondShortestPath ssp = new SecondShortestPath(g, 0, 2);
        System.out.println("Second shortest distance: " + ssp.getPathDist());
        for (DirectedEdge e : ssp.getPathEdges()) {
            System.out.println(e.from() + " → " + e.to() + " (" + e.weight() + ")");
        }
    }
}
