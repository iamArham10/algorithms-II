import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiameterAndCenterOfATree {
    private boolean[] marked;     // marked[v] = True if v is explored
    private int[] edgeTo;         // edgeTo[v] = previous vertex on path to v
    private int[] distTo;         // distTo[v] = distance from source to v
    private int diameter;         // length of the longest path
    private int center;           // center vertex of the tree
    private List<Integer> longestPath; // vertices on the longest path

    public DiameterAndCenterOfATree(UndirectedGraph G) {
        if (G == null) throw new IllegalArgumentException("Graph cannot be null");
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        longestPath = new ArrayList<>();
        processGraph(G);
    }

    // Getters for the computed values
    public int getDiameter() {
        return diameter;
    }

    public int getCenter() {
        return center;
    }

    public List<Integer> getLongestPath() {
        return new ArrayList<>(longestPath);
    }

    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("Vertex " + v + " is not between 0 and " + (V - 1));
    }

    private void bfs(UndirectedGraph G, int s) {
        // Reset arrays for new BFS
        Arrays.fill(marked, false);
        Arrays.fill(distTo, 0);

        validateVertex(s);
        Queue<Integer> queue = new Queue<>();
        marked[s] = true;
        distTo[s] = 0;
        queue.enqueue(s);

        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    queue.enqueue(w);
                }
            }
        }
    }

    private void processGraph(UndirectedGraph G) {
        // First BFS from any vertex (0) to find one end of diameter
        bfs(G, 0);
        int endPoint1 = 0;
        for (int v = 0; v < G.V(); v++) {
            if (distTo[v] > distTo[endPoint1]) {
                endPoint1 = v;
            }
        }

        // Second BFS from the found endpoint to find other end of diameter
        bfs(G, endPoint1);
        int endPoint2 = 0;
        for (int v = 0; v < G.V(); v++) {
            if (distTo[v] > distTo[endPoint2]) {
                endPoint2 = v;
            }
        }

        // Calculate diameter and store the path
        diameter = distTo[endPoint2];
        storeLongestPath(endPoint1, endPoint2);
        findCenter();
    }

    private void storeLongestPath(int start, int end) {
        longestPath.clear();
        for (int v = end; v != start; v = edgeTo[v]) {
            longestPath.add(0, v);
        }
        longestPath.add(0, start);
    }

    private void findCenter() {
        // The center is the middle vertex (or vertices) of the longest path
        int pathSize = longestPath.size();
        if (pathSize % 2 == 0) {
            // If even length, either middle vertex can be the center
            center = longestPath.get(pathSize / 2 - 1);
        }
        else {
            // If odd length, middle vertex is the center
            center = longestPath.get(pathSize / 2);
        }
    }

    // Method to print the results
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Diameter: ").append(diameter).append("\n");
        sb.append("Center vertex: ").append(center).append("\n");
        sb.append("Longest path: ").append(longestPath);
        return sb.toString();
    }
}