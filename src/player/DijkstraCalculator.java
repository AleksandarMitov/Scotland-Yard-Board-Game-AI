// COMS10001/COMS10004
// (code compacted for screen presentation)

// implements Dijkstra's
public class DijkstraCalculator extends GraphCalculator {

  public DijkstraCalculator(Graph<Integer,Integer> graph) {
	super(graph);
  }
  
  // implements Dijkstra's update rule
  protected Double update(Double distance, Double currentDistance, Double directDistance ) {
	  return Math.min(distance, currentDistance + directDistance);
  }
  
  // runs Dijkstra's algorithm and output particular route
  public Graph<Integer,Integer> getResult(Integer startNodeID, Integer destinationNodeID) {
    
	// calculate graph with paths from every node to start node with its distance
	Graph<Integer,Integer> startToAnyNode = getResult(startNodeID);

    // trace route from end node to start node
    Node<Integer> current = startToAnyNode.getNode(destinationNodeID);  
    Graph<Integer,Integer> route = new DirectedGraph<Integer,Integer>();
    route.add(current);
    while (!startToAnyNode.getEdgesFrom(current).isEmpty()) {
      Edge<Integer,Integer> e = startToAnyNode.getEdgesFrom(current).get(0);
      route.add(e.getTarget());
      route.add(e);
      current = e.getTarget();
    }
    return route;
} }