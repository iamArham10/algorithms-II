import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;

public class SecondShortestPath {
    private double pathDist;
    private ArrayList<DirectedEdge> pathEdges;

    public SecondShortestPath(EdgeWeightedDigraph g, int source, int destination) {
        findPath(g, source, destination);
    }

    public double getPathDist() {
        return pathDist;
    }

    public Iterable<DirectedEdge> getPathEdges() {
        return pathEdges;
    }

    private void findPath(EdgeWeightedDigraph g, int source, int destination) {
        EdgeWeightedDigraph gPrime = reverseGraph(g);
        DijkstraSP sp = new DijkstraSP(g, source);
        DijkstraSP spt = new DijkstraSP(gPrime, destination);

        pathDist = Double.POSITIVE_INFINITY;
        int pathVertex = -1;
        for (int v = 0; v < g.V(); v++) {
            if (sp.hasPathTo(v) && spt.hasPathTo(v)) {
                double dist = sp.distTo(v) + spt.distTo(v);
                if (dist < pathDist && dist > sp.distTo(destination)) {
                    pathVertex = v;
                    pathDist = dist;
                }
            }
        }

        Stack<DirectedEdge> pathFromDestToPathVertex = new Stack<>();
        for (DirectedEdge e : spt.pathTo(pathVertex)) {
            pathFromDestToPathVertex.push(new DirectedEdge(e.to(), e.from(), e.weight()));
        }

        pathEdges = new ArrayList<>();
        for (DirectedEdge e : sp.pathTo(pathVertex)) {
            pathEdges.add(e);
        }

        while (!pathFromDestToPathVertex.isEmpty()) {
            pathEdges.add(pathFromDestToPathVertex.pop());
        }
    }

    private EdgeWeightedDigraph reverseGraph(EdgeWeightedDigraph g) {
        EdgeWeightedDigraph reversedGraph = new EdgeWeightedDigraph(g.V());
        for (DirectedEdge e : g.edges()) {
            DirectedEdge reversedEdge = new DirectedEdge(e.to(), e.from(), e.weight());
            reversedGraph.addEdge(reversedEdge);
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
        System.out.println(ssp.getPathDist());
        for (DirectedEdge e : ssp.getPathEdges()) {
            System.out.println(e.from() + " " + e.to() + " " + e.weight());
        }
    }
}