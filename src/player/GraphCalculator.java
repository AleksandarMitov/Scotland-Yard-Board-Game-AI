// COMS10001/COMS10004
// (code compacted for screen presentation)

import java.util.*;

// implements base algorithm for Prim's and Dijkstra's
public abstract class GraphCalculator {

  // reference to input graph
  protected Graph<Integer,Integer> graph;

  // constructor
  public GraphCalculator(Graph<Integer,Integer> graph) {
	this.graph = graph;	  
  }
	
  // applies algorithm to input graph
  public Graph<Integer,Integer> getResult(Integer startNodeID) {
	
	// define node collections
	final Set<Node<Integer>> visited = new HashSet<Node<Integer>>();
	final PriorityQueue<Node<Integer>> unvisited = new PriorityQueue<Node<Integer>>();
	
	// make a result graph
	Graph<Integer,Integer> ourResult = new DirectedGraph<Integer,Integer>();
	
	// initialise starting node
	Node<Integer> currentNode = graph.getNode(startNodeID);
	currentNode.setWeight(0.0);
	visited.add(currentNode);
	
	// initialise node collections
	for (Node<Integer> node : graph.getNodes()) {
	  ourResult.add(node);
	  if (!currentNode.getIndex().equals(node.getIndex())) {
	    node.setWeight(Double.POSITIVE_INFINITY);
	    unvisited.add(node);
	} }
	
	// find initial direct distances to start node
	updateDistances(unvisited, currentNode, ourResult);
	  
	// greedily update nodes
	while (!unvisited.isEmpty()) {
	  visited.add(currentNode);
	  currentNode = unvisited.poll();
	  updateDistances(unvisited, currentNode, ourResult);
	}
	
	// return result
	return ourResult;
  }
  
  // update rule to be specified
  protected abstract Double update(Double distance, Double currentDistance, Double directDistance );

  // updates all unvisited node distances by considering routes via currentNode
  private void updateDistances(PriorityQueue<Node<Integer>> unvisited,
	 	                       Node<Integer> currentNode,
		                       Graph<Integer,Integer> ourResult) {
	
	// consider neighbours of current node (others can't gain from update)
    for(Edge<Integer,Integer> e : graph.getEdgesFrom(currentNode)) {
      Node<Integer> neighbour = e.getTarget();
      
      // only update unvisited nodes (others already have shortest distance)
      if (unvisited.contains(neighbour)) {
   	    Double distance = neighbour.getWeight();
   	    // apply update rule (here with fixed edge weight of 1.0)
        Double possibleUpdate = update(distance, currentNode.getWeight(),1.0); 
        // only update nodes with better option
        if (distance>possibleUpdate) {	
          unvisited.remove(neighbour);
          neighbour.setWeight(possibleUpdate);
          unvisited.add(neighbour); 
          // add reverse edge to result (to create paths to start node rather than from)
          ourResult.add(new Edge<Integer,Integer>(neighbour,currentNode,0));
} } } } }