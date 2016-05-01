package player;
import java.util.*;
import scotlandyard.*;

public class MrXPlayer extends AIMasterRace {
	private final int depthToSimulate = 10;
	private int numberOfPlayers;
	private Move optimalMove = null;
	private List<Colour> playerOrder = new ArrayList<Colour>();
	
	public MrXPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
	}
	
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		numberOfPlayers = view.getPlayers().size();
		System.out.println("Starting number of players: " + numberOfPlayers);
		//now working around a bug with the ScotlandYardView.getPlayers() implementation!? Black player isn't always first
		playerOrder.clear();
		playerOrder.add(Utility.getMrXColour());
		for(Colour player : view.getPlayers()) {
			if(Utility.isPlayerDetective(player)) playerOrder.add(player);
		}
		System.out.println("Starting number of players2: " + playerOrder.size());
		System.out.println("Players playin: ");
		System.out.println(view.getPlayers());
		
				
		Map<Colour, Integer> playersLocations = getPlayersLocations();
		//we're mrX so we should update with our true location that only we know every round
		playersLocations.put(view.getCurrentPlayer(), currentLocation);
		Map<Colour, Map<Ticket, Integer>> playersTickets = getPlayersTickets();
		List<Move> legalMoves = generateMoves(view.getCurrentPlayer(), playersLocations, playersTickets);
		
		//running AI
		//System.out.println("AHA: " + numberOfPlayers);
		miniMaxWithAlphaBetaPruning(0, 0, Long.MIN_VALUE, Long.MAX_VALUE, playersLocations, playersTickets);
		//donezo
		//System.out.println("System generated possible moves:");
		//System.out.println(possibleMoves);
		//System.out.println("AI generated players locations:");
		//System.out.println(playersLocations);
		//System.out.println("AI generated playersTickets:");
		//System.out.println(playersTickets);
		System.out.println("And now AI generated legal moves for player " + view.getCurrentPlayer() + ":");
		System.out.println(legalMoves);
		
		
		//Collections.shuffle(possibleMoves);
		//return possibleMoves.get(0);
		return optimalMove; //result from running the Mini-Max algorithm
	}
	
	@Override
	public String toString() {
		return "Player is of class type: MrXPlayer with colour: " + colour;
	}
	
	private int miniMaxWithAlphaBetaPruning(int depth, int currentPlayer, long alpha, long beta, Map<Colour, Integer> playersLocations, Map<Colour, Map<Ticket, Integer>> playersTickets) {
		//System.out.println("ViewPlayers order: ");
		//System.out.println(playerOrder);
		//System.out.println("Mini-Max depth: " + depth + " for player: " + view.getPlayers().get(currentPlayer));
		//System.out.println("depth: " + depth);
		if(depth == depthToSimulate) {
			//System.out.println("BASE CASE REACHED at depth: " + depthToSimulate);
			//System.out.println("depthToSimulate: " + depthToSimulate);
			return evaluateState(playersLocations, graph); //we've reached the depth, now apply heuristic
		}
		Move currentDepthOtimalMove = null;
		
		Colour player = playerOrder.get(currentPlayer);
		int currentPlayerLocation = playersLocations.get(player);
		List<Move> legalMoves = generateMoves(player, playersLocations, playersTickets);
		//System.out.println("All legal moves for player: " + player + " at depth: " + depth + ", are:");
		//System.out.println(legalMoves);
		//if we get out of bounds, we go back to the starting index
		//System.out.println("NumberOfPlayers: " + numberOfPlayers);
		int nextPlayer = (currentPlayer + 1) % numberOfPlayers;
		int finalScore = 0;
		
		//now handle the different cases by backtracking the different possibilities for making a move:
		if(Utility.isPlayerMrX(player)) {
			//so we're mrX and we gotta win this
			int maximizedScore = Integer.MIN_VALUE;
			Move maximizedMove = MovePass.instance(player);
			//now, simulate each possible move and then dig deeper
			for(Move move : legalMoves) {
				//System.out.println("Exploring move for player: " + player + " at depth: " + depth + ", : " + move);
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
				int heuristicScore = miniMaxWithAlphaBetaPruning(depth + 1, nextPlayer, alpha, beta, playersLocations, playersTickets);
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
			//we're a cop and therefore ZE ENEMY!!!
			int minimizedScore = Integer.MAX_VALUE;
			Move minimizedMove = MovePass.instance(player);
			//now, simulate each possible move and then dig deeper
			for(Move move : legalMoves) {
				//System.out.println("Exploring move for player: " + player + " at depth: " + depth + ", : " + move);
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
				int heuristicScore = miniMaxWithAlphaBetaPruning(depth + 1, nextPlayer, alpha, beta, playersLocations, playersTickets);
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
		if(depth == 0) optimalMove = currentDepthOtimalMove;
		if(depth == 0) System.out.println("Score for the move: " + finalScore);
		return finalScore;
	}
	
	/**
	 * Heuristically evaluate the game state
	 * Higher score indicates better chances for MrX to win
	 * @param playersLocations
	 * @param graph
	 * @return
	 */
	private int evaluateState(Map<Colour, Integer> playersLocations, ScotlandYardGraph graph) {
		//Currently picks random shit
		//TODO: Make some decent implementation
		Integer[] arr = {0, 1, -1};
		List<Integer> numbers = new ArrayList<Integer>(Arrays.asList(arr));
		Collections.shuffle(numbers);
		return numbers.get(0);
	}
}
