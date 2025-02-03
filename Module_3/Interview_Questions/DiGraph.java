/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

import java.util.NoSuchElementException;

public class DiGraph {
    private final int V; // number of vertices
    private int E; // number of edges
    private Bag<Integer>[] adj; // adjacency list

    /**
     * Create an empty graph with V vertices
     *
     * @param v number of vertices
     * @throws IllegalArgumentException if V < 0
     */
    public DiGraph(int v) {
        this.V = v;
        this.E = 0;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new Bag<Integer>();
        }
    }

    /**
     * Initializes the graph from input stream.
     *
     * @param in the input stream
     * @throws IllegalArgumentException if invalid input
     */
    public DiGraph(In in) {
        if (in == null)
            throw new IllegalArgumentException("Argument is null");
        try {
            // reading number of vertices
            this.V = in.readInt();
            if (V < 0)
                throw new IllegalArgumentException("Number of vertices must be non-negative");
            adj = (Bag<Integer>[]) new Bag[V];
            for (int i = 0; i < V; ++i) {
                adj[i] = new Bag<Integer>();
            }

            // reading number of edges
            this.E = in.readInt();
            if (E < 0)
                throw new IllegalArgumentException("Number of vertices must be nonnegative");

            // read edges
            for (int i = 0; i < E; ++i) {
                int s = in.readInt();
                int d = in.readInt();
                validateVertex(s);
                validateVertex(d);
                addEdge(s, d);
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Invalid input format in DiGraph constructor", e);
        }
    }

    /**
     * Validates the vertex
     *
     * @param v the number of vertices in the graph
     * @throws IllegalArgumentException if v < 0
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("Vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * Adds the directed edge s->d to the graph
     *
     * @param s source vertex
     * @param d destination vertex
     * @throws IllegalArgumentException unless both 0 <= s < V and 0 <= d < V
     */
    public void addEdge(int s, int d) {
        validateVertex(s);
        validateVertex(d);
        adj[s].add(d);
        E++;
    }

    /**
     * Returns the vertices adjacent to the vertex v
     *
     * @param v the vertex
     * @return the vertices adjacent to the vertex v
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the number of vertices in the graph
     *
     * @param v the vertex
     * @return the number of vertices in the graph
     * @throws IllegalArgumentException unless 0 <= v < V
     */
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns a string representation of this graph.
     *
     * @return string representation of this graph
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V).append(" vertices, ").append(E).append(" edges").append('\n');
        for (int v = 0; v < V; v++) {
            s.append(v).append(": ");
            for (int w : adj[v]) {
                s.append(w).append(" ");
            }
            s.append('\n');
        }
        return s.toString();
    }
}
