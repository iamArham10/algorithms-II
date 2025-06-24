import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Topological;

import java.util.NoSuchElementException;

public class WordNet {
    // maps synset id to nouns (e.g., 0 -> set[hello, hi])
    private final LinearProbingHashST<Integer, SET<String>> synsetIdToNouns = new LinearProbingHashST<>();
    // maps noun to synset ids (e.g., hello -> set[0, 1, 5])
    private final LinearProbingHashST<String, SET<Integer>> nounToSynsetIds = new LinearProbingHashST<>();

    private final Digraph wordNetGraph;
    private final SAP sapHelper;


    /**
     * Creates a wordnet object
     * @param synsets path of file containing synsets (format: id, nouns, gloss)
     * @param hypernyms path to containing hypernyms (format: idA, idB) for synset id idA -> idB
     */
    public WordNet(String synsets, String hypernyms) {
        try {

        readSynsets(synsets);
            wordNetGraph = new Digraph(synsetIdToNouns.size());
        readHypernyms(hypernyms);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format.");
        }
        Topological topological = new Topological(wordNetGraph);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException("Digraph has cycle");
        }
        int roots = 0;
        for (int i = 0; i < wordNetGraph.V(); i++) {
            if (wordNetGraph.outdegree(i) == 0) {
                roots++;
                if (roots > 1) {
                    throw new IllegalArgumentException("Digraph has "+roots+" roots");
                }
            }
        }
        sapHelper = new SAP(wordNetGraph);
    }

    /**
     * Determines whether word is a noun in wordnet or not
     * @return true if given word is noun
     */
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nounToSynsetIds.contains(word);
    }

    /**
     * returns all the nouns
     * @return SET containing all nouns in Wordnet
     */
    public Iterable<String> nouns() {
        SET<String> allNouns = new SET<>();
        for (String s: nounToSynsetIds.keys()) {
            allNouns.add(s);
        }
        return allNouns;
    }

    /**
     * finds the common ancestor between nounA and nounB in the shortest common path
     * ancestral path
     * @return synset that is the common ancestor between nounA and nounB in the shortest ancestral
     * path
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Invalid argument");
        }
        SET<Integer> idNounAs = nounToSynsetIds.get(nounA);
        SET<Integer> idNounBs = nounToSynsetIds.get(nounB);
        int ancestor = sapHelper.ancestor(idNounAs, idNounBs);
        SET<String> ancestorSynset = synsetIdToNouns.get(ancestor);
        StringBuilder synsetWords = new StringBuilder();
        for (String word: ancestorSynset) {
            synsetWords.append(word+" ");
        }
        return synsetWords.toString();
    }

    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Invalid Argument");
        }

        SET<Integer> idNounAs = nounToSynsetIds.get(nounA);
        SET<Integer> idNounBs = nounToSynsetIds.get(nounB);
        return sapHelper.length(idNounAs, idNounBs);
    }

    private void readSynsets(String synsets) {
        if (synsets == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        In in = new In(synsets);
        String line;
        while (!in.isEmpty()) {
            line = in.readLine();

            String[] fields = line.split(",");

            int synsetId = Integer.parseInt(fields[0]);
            SET<String> nounInSynsets = new SET<>();
            String[] nouns = fields[1].split(" ");
            for (int i = 0; i < nouns.length; i++) {
                nounInSynsets.add(nouns[i]);
                SET<Integer> synsetIdsForNoun;
                if (!nounToSynsetIds.contains(nouns[i])) {
                    synsetIdsForNoun = new SET<>();
                } else {
                    synsetIdsForNoun = nounToSynsetIds.get(nouns[i]);
                }
                synsetIdsForNoun.add(synsetId);
                nounToSynsetIds.put(nouns[i], synsetIdsForNoun);
            }
            synsetIdToNouns.put(synsetId, nounInSynsets);
        }
    }

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
                wordNetGraph.addEdge(v, Integer.parseInt(splits[i]));
        }
    }

    public static void main(String[] args) {
        // not used for assignment
    }
}
