import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Bag;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

public class SAP {
    private static final class BfsCache {
        private final LinkedHashMap<String, String> cache;
        private final int capacity;

        public BfsCache(int capacity) {
            this.capacity = capacity;
            this.cache = new LinkedHashMap<String, String>(capacity, (float) 0.75, true);
        }

        private String createCacheKey(Iterable<Integer> vNodes, Iterable<Integer> wNodes) {
            StringBuilder key = new StringBuilder();
            for (int v: vNodes) {
                key.append(v).append("|");
            }
            key.append(":");
            for (int w: wNodes) {
                key.append(w).append("|");
            }
            return key.toString();
        }

        public String getValue(Iterable<Integer> vNodes, Iterable<Integer> wNodes) {
            String cacheKey = createCacheKey(vNodes, wNodes);
            if (cache.size() >= capacity) {
                String firstKey = cache.keySet().iterator().next();
                cache.remove(firstKey);
            }
            return cache.get(cacheKey);
        }

        public void setValue(Iterable<Integer> vNodes, Iterable<Integer> wNodes,
                             int commonAncestor, int pathLength) {
            String cacheKey = createCacheKey(vNodes, wNodes);
            cache.put(cacheKey, commonAncestor + "," + pathLength);
        }
    }

    private final Digraph digraph;
    private int[] distFromV;
    private int[] distFromW;
    private Bag<Integer> vEntriesChanged;
    private Bag<Integer> wEntriesChanged;
    private int shortestCommonAncestor;
    private int shortestCommonLength;
    private final BfsCache cache;

    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Digraph cannot be null");
        this.digraph = new Digraph(G);
        cache = new BfsCache(G.V() + G.E());
        distFromV = new int[G.V()];
        distFromW = new int[G.V()];
        Arrays.fill(distFromV, -1);
        Arrays.fill(distFromW, -1);
        vEntriesChanged = new Bag<>();
        wEntriesChanged = new Bag<>();
        shortestCommonAncestor = -1;
        shortestCommonLength = Integer.MAX_VALUE;
    }

    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        String value = cache.getValue(Collections.singletonList(v), Collections.singletonList(w));

        if (value != null) {
            return Integer.parseInt(value.split(",")[1]);
        }

        resetQueryState();
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();

        vQueue.enqueue(v);
        distFromV[v] = 0;
        vEntriesChanged.add(v);

        wQueue.enqueue(w);
        distFromW[w] = 0;
        wEntriesChanged.add(w);

        runAlternatingBreadthFirstSearch(vQueue, wQueue);
        if (this.shortestCommonLength != Integer.MAX_VALUE) {
            cache.setValue(Collections.singletonList(v), Collections.singletonList(w),
                           shortestCommonAncestor, shortestCommonLength);
            return shortestCommonLength;
        }
        return -1;
    }

    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        String value = cache.getValue(Collections.singletonList(v), Collections.singletonList(w));
        if (value != null) {
            return Integer.parseInt(value.split(",")[0]);
        }

        resetQueryState();
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();

        vQueue.enqueue(v);
        distFromV[v] = 0;
        vEntriesChanged.add(v);

        wQueue.enqueue(w);
        distFromW[w] = 0;
        wEntriesChanged.add(w);

        runAlternatingBreadthFirstSearch(vQueue, wQueue);
        if (shortestCommonAncestor != -1) {
            cache.setValue(Collections.singletonList(v), Collections.singletonList(w),
                           this.shortestCommonAncestor, this.shortestCommonLength);
        }
        return this.shortestCommonAncestor;
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        String value = cache.getValue(v, w);
        if (value != null) {
            return Integer.parseInt(value.split(",")[1]);
        }
        resetQueryState();
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();

        for (int vNode: v) {
            vQueue.enqueue(vNode);
            distFromV[vNode] = 0;
            vEntriesChanged.add(vNode);
        }
        for (int wNode: w) {
            wQueue.enqueue(wNode);
            distFromW[wNode] = 0;
            wEntriesChanged.add(wNode);
        }

        runAlternatingBreadthFirstSearch(vQueue, wQueue);
        if (this.shortestCommonLength != Integer.MAX_VALUE) {
            cache.setValue(v, w, this.shortestCommonAncestor, shortestCommonLength);
            return shortestCommonLength;
        }
        return -1;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        String value = cache.getValue(v, w);
        if (value != null) {
            return Integer.parseInt(value.split(",")[0]);
        }

        resetQueryState();
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();

        for (int vNode: v) {
            vQueue.enqueue(vNode);
            distFromV[vNode] = 0;
            vEntriesChanged.add(vNode);
        }

        for (int wNode: w) {
            wQueue.enqueue(wNode);
            distFromW[wNode] = 0;
            wEntriesChanged.add(wNode);
        }

        runAlternatingBreadthFirstSearch(vQueue, wQueue);
        if (this.shortestCommonAncestor != -1) {
            cache.setValue(v, w, this.shortestCommonAncestor, this.shortestCommonLength);
        }
        return this.shortestCommonAncestor;
    }

    /**
     *  Resets the modified distance entries from previous query
     */
     private void resetQueryState() {
         // Reset all V-side distances that were changed
         for (int vertex: vEntriesChanged) {
             this.distFromV[vertex] = -1;
         }

         // Reset all W-side distances that were changed
         for (int vertex: wEntriesChanged) {
             this.distFromW[vertex] = -1;
         }

         vEntriesChanged = new Bag<>();
         wEntriesChanged = new Bag<>();

         this.shortestCommonLength = Integer.MAX_VALUE;
         this.shortestCommonAncestor = -1;
    }

    /**
     * Runs alternating BFS from v nodes and w nodes, finds the
     * shortest common path distance
     * @param v Queue containing source v nodes
     * @param w Queue containing source w nodes
     */
    private void runAlternatingBreadthFirstSearch(Queue<Integer> v, Queue<Integer> w) {
        // op that was really good with us and with them as well 
        while (!v.isEmpty() || !w.isEmpty()) {
            // BFS from source v step
            if (!v.isEmpty()) {
                int vertex = v.dequeue();
                // Check if vertex is common with w BFS
                if (this.distFromW[vertex] != -1) {
                    // Calculate the distance
                    int distance = this.distFromW[vertex] + this.distFromV[vertex];
                    if (this.shortestCommonLength > distance) {
                        this.shortestCommonAncestor = vertex;
                        this.shortestCommonLength = distance;
                    }
                }
                for (int neighbor: this.digraph.adj(vertex)) {
                    if (this.distFromV[neighbor] == -1) {
                        this.distFromV[neighbor] = this.distFromV[vertex] + 1;
                        v.enqueue(neighbor);
                        vEntriesChanged.add(neighbor);
                    }
                }
            }

            // BFS from source w step
            if (!w.isEmpty()) {
                int vertex = w.dequeue();
                // Check if vertex is common with v BFS
                if (this.distFromV[vertex] != -1) {
                    int distance = this.distFromV[vertex] + this.distFromW[vertex];
                    if (this.shortestCommonLength > distance) {
                        this.shortestCommonAncestor = vertex;
                        this.shortestCommonLength = distance;
                    }
                }
                for (int neighbor: this.digraph.adj(vertex)) {
                    if (this.distFromW[neighbor] == -1) {
                        this.distFromW[neighbor] = this.distFromW[vertex] + 1;
                        w.enqueue(neighbor);
                        wEntriesChanged.add(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Validate the given vertex between 0 and V-1
     * @param vertex vertex id
     */
    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= digraph.V())
            throw new IllegalArgumentException("vertex " + vertex + " is not between 0 and " + (digraph.V()-1));
    }

    /**
     * Validate the given vertices between 0 and V-1
     * @param vertices iterable of vertices
     */
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) throw new IllegalArgumentException("vertices cannot be null");
        for (Integer v: vertices) {
            if (v == null) throw new IllegalArgumentException("vertex cannot be null");
            validateVertex(v);
        }
    }

    public static void main(String[] args) {
        Digraph g = new Digraph(6);
        g.addEdge(1, 0);
        g.addEdge(2, 1);
        g.addEdge(0, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        g.addEdge(3, 5);
        SAP sap = new SAP(g);
        int length = sap.length(2, 0);
        int ancestor = sap.ancestor(2, 0);
        System.out.println("Length: " + length);
        System.out.println("Ancestor: " + ancestor);
    }
}