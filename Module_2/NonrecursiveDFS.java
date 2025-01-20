import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Non-recursive implementation of Depth-First Search.
 * Uses a stack to simulate recursion.
 */
public class NonrecursiveDFS {
    private boolean[] marked;    // marked[v] = is there an s-v path?

    /**
     * Computes the vertices connected to the source vertex s in graph G.
     *
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException if source vertex is invalid
     */
    public NonrecursiveDFS(UndirectedGraph G, int s) {
        marked = new boolean[G.V()];  // Changed from Boolean to boolean
        validateVertex(s);

        // Use a stack instead of recursion
        Stack<Integer> stack = new Stack<Integer>();
        marked[s] = true;
        stack.push(s);

        while (!stack.isEmpty()) {
            int v = stack.pop();
            StdOut.println(v
                                   + "Popped out of stack");  // Print before processing adjacent for better tracing

            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    stack.push(w);
                }
            }
        }
    }

    /**
     * Is vertex v connected to the source vertex?
     *
     * @param v the vertex
     * @return true if there is a path, false otherwise
     * @throws IllegalArgumentException if vertex is invalid
     */
    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        UndirectedGraph G = new UndirectedGraph(in);
        NonrecursiveDFS gf = new NonrecursiveDFS(G, 0);
        for (int v = 0; v < G.V(); v++) {
            if (gf.marked(v))
                StdOut.print(v + " ");
        }
        StdOut.println();
    }
}