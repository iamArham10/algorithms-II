import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

/**
 * An undirected graph implementation using adjacency lists.
 * This implementation allows for self-loops but not parallel edges.
 */
public class UndirectedGraph {
    private static final String NEWLINE = System.getProperty("line.separator");
    private final int V;          // number of vertices
    private int E;                // number of edges
    private Bag<Integer>[] adj;   // adjacency lists

    /**
     * Initializes an empty graph with V vertices and 0 edges.
     *
     * @param V number of vertices
     * @throws IllegalArgumentException if V < 0
     */
    @SuppressWarnings("unchecked")
    public UndirectedGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {  // Fixed the loop condition from V++ to v++
            adj[v] = new Bag<Integer>();
        }
    }

    /**
     * Initializes a graph from an input stream.
     *
     * @param in the input stream
     * @throws IllegalArgumentException if the input is invalid
     */
    @SuppressWarnings("unchecked")
    public UndirectedGraph(In in) {
        if (in == null) throw new IllegalArgumentException("argument is null");
        try {
            this.V = in.readInt();
            if (V < 0) throw new IllegalArgumentException("number of vertices must be nonnegative");

            adj = (Bag<Integer>[]) new Bag[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new Bag<Integer>();
            }

            int E = in.readInt();
            if (E < 0) throw new IllegalArgumentException("number of edges must be nonnegative");

            for (int i = 0; i < E; i++) {
                int v = in.readInt();
                int w = in.readInt();
                validateVertex(v);
                validateVertex(w);
                addEdge(v, w);
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in graph constructor", e);
        }
    }

    /**
     * Initializes a new graph that is a deep copy of G.
     *
     * @param G the graph to copy
     * @throws IllegalArgumentException if G is null
     */
    @SuppressWarnings("unchecked")
    public UndirectedGraph(UndirectedGraph G) {
        if (G == null) throw new IllegalArgumentException("argument is null");

        this.V = G.V();
        this.E = G.E();

        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Integer>();
        }

        for (int v = 0; v < V; v++) {
            Stack<Integer> reverse = new Stack<Integer>();
            for (int w : G.adj(v)) {
                reverse.push(w);
            }
            for (int w : reverse) {
                adj[v].add(w);
            }
        }
    }

    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this graph.
     *
     * @return the number of edges in this graph
     */
    public int E() {
        return E;
    }

    /**
     * Validates that vertex v is a valid vertex.
     *
     * @param v vertex to validate
     * @throws IllegalArgumentException if v is not a valid vertex
     */
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * Adds the undirected edge v-w to this graph.
     *
     * @param v one vertex in the edge
     * @param w the other vertex in the edge
     * @throws IllegalArgumentException if either vertex is invalid
     */
    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        E++;
        adj[v].add(w);
        adj[w].add(v);
    }

    /**
     * Returns the vertices adjacent to vertex v.
     *
     * @param v vertex
     * @return the vertices adjacent to vertex v
     * @throws IllegalArgumentException if v is not a valid vertex
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the degree of vertex v.
     *
     * @param v vertex
     * @return the degree of vertex v
     * @throws IllegalArgumentException if v is not a valid vertex
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
        s.append(V).append(" vertices, ").append(E).append(" edges").append(NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v).append(": ");
            for (int w : adj[v]) {
                s.append(w).append(" ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        UndirectedGraph G = new UndirectedGraph(in);
        StdOut.println(G);
    }
}