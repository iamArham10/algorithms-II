/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

public class MinimumWeightFeedbackEdge {
    EdgeWeightedGraph edgeWeightedGraph;
    Bag<Edge> feedbackEdges;

    public MinimumWeightFeedbackEdge(EdgeWeightedGraph g) {
        this.edgeWeightedGraph = new EdgeWeightedGraph(g);
        this.feedbackEdges = new Bag<>();
    }

    private void findMinimumFeedBackEdges() {


    }
}
