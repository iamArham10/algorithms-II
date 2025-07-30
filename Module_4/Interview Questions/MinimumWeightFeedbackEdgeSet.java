/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MinimumWeightFeedbackEdgeSet {
    private Bag<Edge> edgeSet;

    MinimumWeightFeedbackEdgeSet(EdgeWeightedGraph G) {
        PrimsMaxMst maxMst = new PrimsMaxMst(G);
        edgeSet = new Bag<>();
        Set<Edge> maxMSTEdges = new HashSet<>();
        for (Edge e : maxMst.edges()) {
            maxMSTEdges.add(e);
        }

        for (Edge e : G.edges()) {
            if (!maxMSTEdges.contains(e))
                edgeSet.add(e);
        }
    }

    public Iterator<Edge> getEdgeSet() {
        return edgeSet.iterator();
    }

    public static void main(String[] args) {

    }
}
