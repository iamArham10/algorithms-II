import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAP {
    private final Digraph G;

    // marks nodes that are reachable from bfs starting from v
    private final int[] vEdgeTo;

    // marks nodes that are reachable from bfs starting from u
    private final int[] wEdgeTo;

    // stores nodes that are visited from current query
    private final Stack<Integer> vMarked;
    private final Stack<Integer> wMarked;

    private int shortestCommonAncestor;
    private int shortestCommonAncestorPath;

    private final Map<String, String> queryCache;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
        vEdgeTo = new int[G.V()];
        wEdgeTo = new int[G.V()];
            for (int i = 0; i < G.V(); i++) {
            // none of them are marked
            vEdgeTo[i] = -1;
            wEdgeTo[i] = -1;
        }
        vMarked = new Stack<>();
        wMarked = new Stack<>();
        queryCache = new HashMap<>();
    }

    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        String Key = buildCacheKey(v, w);
        if (queryCache.containsKey(Key)) {
            String result =  queryCache.get(Key);
            return  Integer.parseInt(result.split(",")[1]);
        }
        findSAP(List.of(v), List.of(w));
        queryCache.put(Key, Integer.toString(shortestCommonAncestor) +  "," + Integer.toString(shortestCommonAncestorPath));
        return shortestCommonAncestorPath;
    }

    private String buildCacheKey(int v, int w) {
        return Integer.toString(v) + "," + "|" + Integer.toString(w) + ",";
    }

    private String buildCacheKey(Iterable<Integer> v, Iterable<Integer> w) {
        StringBuilder sb = new StringBuilder();

         for (Integer vv : v) {
            sb.append(vv);
            sb.append(",");
        }

        // adding the separator
        sb.append("|");

        for (Integer ww : w) {
            sb.append(ww);
            sb.append(",");
        }
        return sb.toString();
    }

    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        String key = buildCacheKey(v, w);
        if (queryCache.containsKey(key)) {
            return Integer.parseInt(queryCache.get(key).split(",")[0]);
        }
        findSAP(List.of(v), List.of(w));
        queryCache.put(key, Integer.toString(shortestCommonAncestor) + "," + Integer.toString(shortestCommonAncestorPath));
        return shortestCommonAncestor;
    }

    private void findSAP(Iterable<Integer> v, Iterable<Integer> w) {
        // Clear previous state
        while (!vMarked.isEmpty()) {
            vEdgeTo[vMarked.pop()] = -1;
        }
        while (!wMarked.isEmpty()) {
            wEdgeTo[wMarked.pop()] = -1;
        }
        shortestCommonAncestor = -1;
        shortestCommonAncestorPath = -1;

        // running two bfs alternatively
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();

        for (Integer i : v) {
            validateVertex(i);
            vQueue.enqueue(i);
            vMarked.push(i);
            vEdgeTo[i] = 0;
        }

        for (Integer i : w) {
            validateVertex(i);
            wQueue.enqueue(i);
            wMarked.push(i);
            wEdgeTo[i] = 0;

            // Check if this vertex is already in the first set (immediate common ancestor)
            if (vEdgeTo[i] != -1) {
                shortestCommonAncestor = i;
                shortestCommonAncestorPath = 0;  // Direct path from v to w
                return;
            }
        }

        bfs(vQueue, wQueue);

        if (shortestCommonAncestor != -1) {
            shortestCommonAncestorPath = backTrack();
        }
    }

    private int backTrack() {
        int ancestor = shortestCommonAncestor;
        int distance = 0;

        // Calculate distance from v to ancestor
        while (vEdgeTo[ancestor] != 0) {
            distance++;
            ancestor = vEdgeTo[ancestor];
        }

        ancestor = shortestCommonAncestor;
        while (wEdgeTo[ancestor] != 0) {
            distance++;
            ancestor = wEdgeTo[ancestor];
        }

        return distance;
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        String query = buildCacheKey(v, w);
        if (queryCache.containsKey(query)) {
            return Integer.parseInt(queryCache.get(query).split(",")[1]);
        }
        findSAP(v, w);
        queryCache.put(query, Integer.toString(shortestCommonAncestor) + "," +
                Integer.toString(shortestCommonAncestorPath));
        return shortestCommonAncestorPath;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        String query = buildCacheKey(v, w);
        if (queryCache.containsKey(query)) {
            return  Integer.parseInt(queryCache.get(query).split(",")[0]);
        }
        findSAP(v, w);
        queryCache.put(query, Integer.toString(shortestCommonAncestor) + "," + Integer.toString(shortestCommonAncestorPath));
        return shortestCommonAncestor;
    }

    private void bfs(Queue<Integer> vQueue, Queue<Integer> wQueue) {
        while (!vQueue.isEmpty() || !wQueue.isEmpty()) {
            // Process one level from v's BFS
            if (!vQueue.isEmpty()) {
                int node = vQueue.dequeue();
                for (Integer w : G.adj(node)) { // If w is visited by wQueue then it's common ancestor
                    if (wEdgeTo[w] != -1) {
                        // w is the common ancestor, first one found in alternating BFS is guaranteed shortest
                        shortestCommonAncestor = w;
                        vEdgeTo[w] = node;
                        vMarked.push(w);
                        return;
                    }
                    else if (vEdgeTo[w] == -1) {
                        // w is not visited by this bfs add it to queue
                        vQueue.enqueue(w);
                        vEdgeTo[w] = node;
                        vMarked.push(w);
                    }
                }
            }

            // Process one level from w's BFS
            if (!wQueue.isEmpty()) {
                int node = wQueue.dequeue();
                for (Integer v : G.adj(node)) {
                    if (vEdgeTo[v] != -1) {
                        // v is visited by vQueue, then it's common ancestor
                        shortestCommonAncestor = v;
                        wEdgeTo[v] = node;
                        wMarked.push(v);

                        return;
                    }
                    else if (wEdgeTo[v] == -1) {
                        // node is not visited by current queue bfs
                        wQueue.enqueue(v);
                        wEdgeTo[v] = node;
                        wMarked.push(v);
                    }

                }
            }
        }

        // No common ancestor found
        shortestCommonAncestor = -1;
    }

    private void validateVertex(int v) {
        int V = G.V();
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    public void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Integer v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
    }

    public static void main(String[] args) {
        Digraph g = new Digraph(12);
        g.addEdge(10, 9);
        g.addEdge(11, 9);
        g.addEdge(8, 5);
        g.addEdge(9, 5);
        g.addEdge(5, 1);
        g.addEdge(4, 1);
        g.addEdge(7, 3);
        g.addEdge(6, 3);
        g.addEdge(3, 1);
        g.addEdge(1, 0);
        g.addEdge(2, 0);
        SAP sap = new SAP(g);
        System.out.println(sap.ancestor(List.of(3, 7, 6, 1), List.of(10, 11, 2)));
    }
}