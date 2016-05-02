package player;
import java.util.*;
import graph.*;
import scotlandyard.*;

public class DetectivePlayer extends AIMasterRace {
	/**
	 * How many moves deep should the AI look
	 */
	private final int depthToSimulate = 8;
	
	/**
	 * Place to hold our computed move from the AI
	 */
	private Move optimalMove = null;
	
	/**
	 * Holds the distances between pairs of nodes in the game graph
	 * the distance is basically the number of single moves you'd have to make from the
	 * first node to reach the second
	 */
	private Map<Integer, Map<Integer, Integer>> distances;
	
	public DetectivePlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
		distances = runBFSOnGameGraph();
	}
	
	/**
	 * Implements the AIMasterRace abstract class method
	 * Uses Iterative deepening to find a winning move as fast as possible
	 */
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		//running AI
		for(int i = 1; i <= depthToSimulate; ++i) {
			Map<Colour, Integer> playersLocations = getPlayersLocations();
			Map<Colour, Map<Ticket, Integer>> playersTickets = getPlayersTickets();	
			long score = miniMaxWithAlphaBetaPruning(i, colour, true, Long.MIN_VALUE, Long.MAX_VALUE, playersLocations, playersTickets);
			System.out.println("For depth: " + i + ", the optimal move is: " + optimalMove);
			if(score == Long.MAX_VALUE) break;
		}
		//donezo
		return optimalMove; //result from running the Mini-Max, Alpha-Beta pruning, award winning algorithm
	}
	
	/**
	 * Implementation of a Mini-Max algorithm with Alpha-Beta pruning of non-optimal states
	 * Memory is also pretty efficiently used since when I'm done digging I backtrack and revert the existing
	 * state back to normal thus avoiding the creation of lots of data for each state
	 * @param depth the current depth in the game state
	 * @param currentPlayer the player we're simulating
	 * @param alpha the alpha value
	 * @param beta the beta value
	 * @param playersLocations a map holding the location for each player
	 * @param playersTickets a map holding the tickets for each player
	 * @return
	 */
	private long miniMaxWithAlphaBetaPruning(int depth, Colour whichDetective, boolean isDetectiveTurn, long alpha, long beta, Map<Colour, Integer> playersLocations, Map<Colour, Map<Ticket, Integer>> playersTickets) {
		if(mrXIsBusted(playersLocations) && !isDetectiveTurn) return Long.MAX_VALUE; //that's what we're aiming for
		if(depth == 0) return evaluateState(playersLocations, graph, playersTickets); //we've reached the depth, now apply heuristic
		Move currentDepthOtimalMove = null;
		
		Colour player = isDetectiveTurn ? whichDetective : Utility.getMrXColour();
		int currentPlayerLocation = playersLocations.get(player);
		List<Move> legalMoves = generateMoves(player, playersLocations, playersTickets);
		long finalScore = 0;
		
		//now handle the different cases by backtracking the different possibilities for making a move:
		if(isDetectiveTurn) {
			//so we're a detective and we gotta win this
			long maximizedScore = Integer.MIN_VALUE;
			Move maximizedMove = MovePass.instance(player);
			//now, simulate each possible move and then dig deeper
			for(Move move : legalMoves) {
				List<Ticket> necessaryTickets = Utility.getNecessaryTickets(move);
				int endLocation = Utility.getMoveEndLocation(currentPlayerLocation, move);
				//now should recall that AIMasterRace#generateMoves already makes sure we have the tickets for the move
				//simulate using the tickets
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) - 1); //reduce number by 1
				}
				//simulate going to end location
				playersLocations.put(player, endLocation);
				//we're ready to dive in!
				long heuristicScore = miniMaxWithAlphaBetaPruning(depth - 1, whichDetective, !isDetectiveTurn, alpha, beta, playersLocations, playersTickets);
				if(heuristicScore > maximizedScore) {
					//we've found a better move :)
					maximizedScore = heuristicScore;
					maximizedMove = move;
				}
				alpha = Math.max(alpha, maximizedScore);
				
				//we're done examining this scenario, now revert changes
				//return tickets:
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) + 1); //increase number by 1
				}
				//return to original location
				playersLocations.put(player, currentPlayerLocation);
				currentDepthOtimalMove = maximizedMove; //retain optimal move
				if(beta <= alpha) break; //beta cut-off
			}
			finalScore = maximizedScore;
		}
		else {
			//we're mrX and therefore ZE ENEMY!!!
			long minimizedScore = Integer.MAX_VALUE;
			Move minimizedMove = MovePass.instance(player);
			//now, simulate each possible move and then dig deeper
			for(Move move : legalMoves) {
				List<Ticket> necessaryTickets = Utility.getNecessaryTickets(move);
				int endLocation = Utility.getMoveEndLocation(currentPlayerLocation, move);
				//now should recall that AIMasterRace#generateMoves already makes sure we have the tickets for the move
				//simulate using the tickets
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) - 1); //reduce number by 1
				}
				//simulate going to end location
				playersLocations.put(player, endLocation);
				//we're ready to dive in!
				long heuristicScore = miniMaxWithAlphaBetaPruning(depth - 1, whichDetective, !isDetectiveTurn, alpha, beta, playersLocations, playersTickets);
				if(heuristicScore < minimizedScore) {
					//we've found a better move :)
					minimizedScore = heuristicScore;
					minimizedMove = move;
				}
				beta = Math.min(beta, minimizedScore);
				//we're done examining this scenario, now revert changes
				//return tickets:
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) + 1); //increase number by 1
				}
				//return to original location
				playersLocations.put(player, currentPlayerLocation);
				currentDepthOtimalMove = minimizedMove; //retain optimal move
				if(beta <= alpha) break; //alpha cut-off
			}
			finalScore = minimizedScore;
		}
		//System.out.println("Optimal move for player: " + player + " at depth: " + depth + " is: " + currentDepthOtimalMove);
		if(isDetectiveTurn) {
			optimalMove = currentDepthOtimalMove;
		}
		return finalScore;
	}
	
	/**
	 * Heuristically evaluate the game state
	 * Higher score indicates better chances for the detective to win
	 * We tale into account the edge distance to mr.X and the
	 * available types of transport from that position
	 * @param playersLocations
	 * @param graph
	 */
	private long evaluateState(Map<Colour, Integer> playersLocations, ScotlandYardGraph graph, Map<Colour, Map<Ticket, Integer>> playersTickets) {
		int detectiveLocation = playersLocations.get(colour);
		int mrXLocation = playersLocations.get(Utility.getMrXColour());
		int distance = 0;
		//if he's been revealed at least once
		if(mrXLocation != 0) distance = distances.get(detectiveLocation).get(mrXLocation);
		long heuristic = 0;
		heuristic = -40*distance;
		
		//now if there are more means of transport on detective's position, increase the value of the score
		//after all, the more options, the better plan we can conceive
		List<Edge<Integer, Transport>> edges = graph.getEdgesFrom(graph.getNode(detectiveLocation));
		for(Edge<Integer, Transport> edge : edges) {
			Transport kindOfTransport = edge.getData();
			int value = 10;
			switch(kindOfTransport) {
			case Underground:
				value = 20;
				break;
			case Bus:
				value = 10;
				break;
			}
			heuristic += value;
		}
		
		return heuristic;
	}
	
	/**
	 * Runs a BFS from each node to find all-pairs distances in O(N^2) total time complexity
	 */
	private Map<Integer, Map<Integer, Integer>> runBFSOnGameGraph() {
		Map<Integer, Map<Integer, Integer>> result = new HashMap<Integer, Map<Integer, Integer>>();
		List<Node<Integer>> graphNodes = graph.getNodes();
		for(Node<Integer> node : graphNodes) {
			int mapLocation = node.getIndex();
			Map<Integer, Integer> distancesToRestOfNodes = BFS(node);
			result.put(mapLocation, distancesToRestOfNodes);
		}
		return result;
	}
	
	/**
	 * We apply a heuristic: We ignore all the edges (so possible paths) that use
	 * the Underground or a boat as a means of transport!
	 * Runs a Breadth-First search starting from the root node to find
	 * the distances to every other node in the graph. Runs in O(N) time and memory
	 * @param rootNode
	 * @return a map holding the distances from root to every node in the graph 
	 */
	private Map<Integer, Integer> BFS(Node<Integer> rootNode) {
		Set<Node<Integer>> visitedNodes = new HashSet<Node<Integer>>();
		//key is a node index, value is distance(root, currentNodeIndex)
		Map<Integer, Integer> distancesFromRoot = new HashMap<Integer, Integer>();
		distancesFromRoot.put(rootNode.getIndex(), 0);
		visitedNodes.add(rootNode);
		Queue<Node<Integer>> queue = new LinkedList<Node<Integer>>();
		queue.add(rootNode);
		while(!queue.isEmpty()) {
			Node<Integer> currentNode = queue.remove();
			List<Edge<Integer, Transport>> edges = graph.getEdgesFrom(currentNode);
			for(Edge<Integer, Transport> edge : edges) {
				Transport kindOfTransport = edge.getData();
				//now if the means of transport is Underground or a boat, we skip the edge
				if(kindOfTransport == Transport.Underground || kindOfTransport == Transport.Boat) continue;
				Node<Integer> target = edge.getTarget();
				if(visitedNodes.contains(target)) continue; //we skip already visited nodes
				//now set distance from the root to target using currentNode
				distancesFromRoot.put(target.getIndex(), distancesFromRoot.get(currentNode.getIndex()) + 1);
				visitedNodes.add(target);
				queue.add(target);
			}
		}
		return distancesFromRoot;
	}
	
	/**
	 * Checks if mr.X is on a position that is also occupied by a detective
	 * @param playersLocations
	 */
	private boolean mrXIsBusted(Map<Colour, Integer> playersLocations) {
		for(Colour player : playersLocations.keySet()) {
			if(Utility.isPlayerDetective(player) && playersLocations.get(player).equals(playersLocations.get(Utility.getMrXColour()))) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Player is of class type: DetectivePlayer with colour: " + colour;
	}
	
}
