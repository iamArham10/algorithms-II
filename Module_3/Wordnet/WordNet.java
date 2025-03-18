import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;

public class WordNet {
    // graph for representing relationship between synsets
    private final Digraph G;
    // Hashmap maps each word to bag of synset id's that it belongs to
    private final HashMap<String, Bag<Integer>> nounToSynsetId;
    // Hashmap maps each synset id to nouns separated by space
    private final HashMap<Integer, String> synsetIdToNoun;

    public WordNet(String synsets, String hypernyms) {
        nounToSynsetId = new HashMap<>();
        synsetIdToNoun = new HashMap<>();
        readSynsets(synsets);
        G = new Digraph(nounToSynsetId.size());
        readHypernyms(hypernyms);
        if (!CheckDAG(G)) {
            throw new IllegalArgumentException("Graph is not rooted DAG");
        }
    }

    /*
     * Returns whether word is a noun or not in the WordNet
     */
    public boolean isNouns(String word) {
        return nounToSynsetId.containsKey(word);
    }

    /*
     * Returns all the nouns in the WordNet
     */
    public Iterable<String> nouns() {
        HashSet<String> nouns = new HashSet<>();
        for (String s : nounToSynsetId.keySet()) {
            nouns.add(s);
        }
        return nouns;
    }

    public String sap(String nounA, String nounB) {
        return null;
    }

    public String distance(String nounA, String nounB) {
        return null;
    }


    /*
     * Processes the synsets file, reads the synsets stores the id and respective
     * nouns and their relations in two hashmaps
     */
    private void readSynsets(String synsets) {
        if (synsets == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        In in = new In(synsets);
        String line;
        while (!in.isEmpty()) {
            line = in.readLine();
            String[] splits = line.split(",");
            int synsetId = Integer.parseInt(splits[0]);
            String nouns = splits[1];

            // synset id to nouns
            synsetIdToNoun.put(synsetId, nouns);

            // noun to synset id's
            for (String noun : nouns.split(" ")) {
                // if noun isn't already mapped to bag of id's
                if (!nounToSynsetId.containsKey(noun)) {
                    Bag<Integer> synsetIdBag = new Bag<>();
                    synsetIdBag.add(synsetId);
                    nounToSynsetId.put(noun, synsetIdBag);
                }
                else
                    // adding id to the nouns synset bag
                    nounToSynsetId.get(noun).add(synsetId);
            }
        }
    }

    /*
     * Processes the hypernyms file, adds the edges between synset id's provided
     */
    private void readHypernyms(String hypernyms) {
        if (hypernyms == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        In in = new In(hypernyms);
        String line;
        while (in.hasNextLine()) {
            line = in.readLine();
            String[] splits = line.split(",");
            int v = Integer.parseInt(splits[0]);
            for (int i = 1; i < splits.length; i++)
                G.addEdge(v, Integer.parseInt(splits[i]));
        }
    }

    private boolean CheckDAG(Digraph G) {
        // checking whether the graph has only one root
        int[] indegree = new int[G.V()];

        for (int v = 0; v < G.V(); ++v) {
            for (int w : G.adj(v)) {
                indegree[w]++;
            }
        }

        boolean root = false;
        for (int v = 0; v < G.V(); ++v) {
            if (indegree[v] == 0) {
                if (root) {
                    return false;
                }
                root = true;
            }
        }

        /// checking whether graph contains a cycle
        boolean[] visited = new boolean[G.V()];
        boolean[] onStack = new boolean[G.V()];

        for (int v = 0; v < G.V(); ++v) {
            if (!visited[v] && checkCycle(G, v, visited, onStack)) {
                // cycle detected
                return false;
            }
        }
        return true;
    }

    private boolean checkCycle(Digraph G, int v, boolean[] visited, boolean[] onStack) {
        visited[v] = true;
        onStack[v] = true;
        for (int w : G.adj(v)) {
            if (onStack[w]) {
                return true;
            }
            if (!visited[w]) {
                if (checkCycle(G, w, visited, onStack)) {
                    return true;
                }
            }
        }
        onStack[v] = false;
        return false;
    }
}
