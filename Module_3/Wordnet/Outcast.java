public class Outcast {
    private final WordNet wordnet;
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Returns the outcast noun from the given array of nouns.
     * @param nouns the array of nouns
     * @return the outcast noun
     */
    public String outcast(String[] nouns) {
       int maxDistance = Integer.MIN_VALUE; // minimum value of int
       String outcast = "";
       for (int i = 0; i < nouns.length; i++) {
           String wordI = nouns[i];
           int distanceI = 0;
           for (int j = 0; j < i; j++) {
               distanceI += wordnet.distance(nouns[j], nouns[i]);
           }
           for (int j = i+1; j < nouns.length; j++) {
               distanceI += wordnet.distance(nouns[i], nouns[j]);
           }
           if (distanceI > maxDistance) {
               maxDistance = distanceI;
               outcast = wordI;
           }
       }
       return outcast;
    }

    public static void main(String[] args) {
        // Main method intentionally left empty for this assignment
    }
}
