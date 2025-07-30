import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;

public class EdgeInMST {

    private EdgeWeightedGraph edgeWeightedGraph;

    public EdgeInMST(EdgeWeightedGraph g) {
        this.edgeWeightedGraph = new EdgeWeightedGraph(g);
    }

    public boolean isEdgeInMst(Edge e) {
        if (!validateEdge(e)) return false;
        return !dfsCheck(e);


    }

    public boolean validateEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        for (Edge adj : this.edgeWeightedGraph.adj(v)) {
            if (adj.other(v) == w && adj.weight() == e.weight())
                return true;
        }
        return false;
    }

    public boolean dfsCheck(Edge e) {
        Iterable<Edge> path = alternativePath(e);
        if (path != null) {
            for (Edge pathEdge : path) {
                if (pathEdge.weight() > e.weight()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Iterable<Edge> alternativePath(Edge e) {
        boolean[] marked = new boolean[this.edgeWeightedGraph.V()];
        Edge[] prev = new Edge[this.edgeWeightedGraph.V()];
        Arrays.fill(prev, null);
        int v = e.either();
        int w = e.other(v);
        Stack<Integer> stack = new Stack<>();
        marked[v] = true;
        stack.push(v);
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (vertex == w) {
                Stack<Edge> pathStack = new Stack<Edge>();
                int current = w;
                while (prev[current] != null) {
                    pathStack.push(prev[current]);
                    w = prev[current].other(current);
                }
                ArrayList<Edge> path = new ArrayList<>();
                while (!pathStack.isEmpty()) {
                    path.add(pathStack.pop());
                }
                return path;
            }
            for (Edge adj : this.edgeWeightedGraph.adj(vertex)) {
                if (marked[adj.other(vertex)]) continue;
                if (!adj.equals(e) && adj.weight() < e.weight()) {
                    int adjVertex = adj.other(vertex);
                    marked[adjVertex] = true;
                    prev[adjVertex] = adj;
                    stack.push(adjVertex);
                }
            }
        }
        return null;
    }


}

