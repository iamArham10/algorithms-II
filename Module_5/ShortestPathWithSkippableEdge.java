import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;

public class ShortestPathWithSkippableEdge {
    private DirectedEdge skippedEdge;
    private ArrayList<DirectedEdge> path;
    private double pathDistance;

    public ShortestPathWithSkippableEdge(EdgeWeightedDigraph g, int source, int destination) {
        path = new ArrayList<>();
        pathDistance = Double.POSITIVE_INFINITY;
        skippedEdge = null;
        EdgeWeightedDigraph reversedGraph = reverseGraph(g);
        DijkstraSP spSource = new DijkstraSP(g, source);
        DijkstraSP spDestination = new DijkstraSP(reversedGraph, destination);
        skippedEdge = findSkippableEdge(g, spSource, spDestination);
        if (skippedEdge != null) {
            buildPath(spSource, spDestination);
            pathDistance = spSource.distTo(skippedEdge.from()) + spDestination.distTo(
                    skippedEdge.to());
        }
    }

    public double getpathDistance() {
        return pathDistance;
    }

    public Iterable<DirectedEdge> getPath() {
        return path;
    }

    public DirectedEdge getSkippedEdge() {
        return skippedEdge;
    }

    private void buildPath(DijkstraSP spSource, DijkstraSP spDestination) {
        for (DirectedEdge e : spSource.pathTo(skippedEdge.from())) {
            path.add(e);
        }

        Stack<DirectedEdge> stack = new Stack<>();
        for (DirectedEdge e : spDestination.pathTo(skippedEdge.to())) {
            stack.push(new DirectedEdge(e.to(), e.from(), e.weight()));
        }
        path.add(skippedEdge);
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }
    }

    private static DirectedEdge findSkippableEdge(EdgeWeightedDigraph g, DijkstraSP spSource,
                                                  DijkstraSP spDestination) {
        DirectedEdge zeroEdge = null;
        double pathLenght = Double.POSITIVE_INFINITY;
        for (DirectedEdge e : g.edges()) {
            int from = e.from();
            int to = e.to();
            if (spSource.hasPathTo(from) && spDestination.hasPathTo(to)) {
                if (spSource.distTo(from) + spDestination.distTo(to) < pathLenght) {
                    pathLenght = spSource.distTo(from) + spDestination.distTo(to);
                    zeroEdge = e;
                }
            }
        }
        return zeroEdge;
    }

    private EdgeWeightedDigraph reverseGraph(EdgeWeightedDigraph g) {
        EdgeWeightedDigraph reversedGraph = new EdgeWeightedDigraph(g.V());
        for (DirectedEdge e : g.edges()) {
            reversedGraph.addEdge(new DirectedEdge(e.to(), e.from(), e.weight()));
        }
        return reversedGraph;
    }

    public static void main(String[] args) {

    }
}
