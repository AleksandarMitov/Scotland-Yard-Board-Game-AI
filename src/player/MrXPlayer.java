package player;
import java.util.*;
import scotlandyard.*;

public class MrXPlayer extends AIMasterRace {
	private final int depthToSimulate = 3;
	private Move optimalMove = null;
	
	public MrXPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
	}
	
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		Map<Colour, Integer> playersLocations = getPlayersLocations();
		//we're mrX so we should update with our true location that only we know every round
		playersLocations.put(view.getCurrentPlayer(), currentLocation);
		Map<Colour, Map<Ticket, Integer>> playersTickets = getPlayersTickets();
		List<Move> legalMoves = generateMoves(view.getCurrentPlayer(), playersLocations, playersTickets);
		
		System.out.println("System generated possible moves:");
		System.out.println(possibleMoves);
		System.out.println("AI generated players locations:");
		System.out.println(playersLocations);
		System.out.println("AI generated playersTickets:");
		System.out.println(playersTickets);
		System.out.println("And now AI generated legal moves for player " + view.getCurrentPlayer() + ":");
		System.out.println(legalMoves);
		
		
		Collections.shuffle(possibleMoves);
		return possibleMoves.get(0);
	}
	
	@Override
	public String toString() {
		return "Player is of class type: MrXPlayer with colour: " + colour;
	}
	
	private int miniMax(int depth, int currentPlayer, Map<Colour, Integer> playersLocations, Map<Colour, Map<Ticket, Integer>> playersTickets) {
		if(currentPlayer == 0 && depth == depthToSimulate*numberOfPlayers) return evaluateState(playersLocations, graph); //we've reached the depth, now apply heuristic
		Move currentDepthOtimalMove = null;
		
		Colour player = view.getPlayers().get(currentPlayer);
		int currentPlayerLocation = playersLocations.get(player);
		List<Move> legalMoves = generateMoves(player, playersLocations, playersTickets);
		//if we get out of bounds, we go back to the starting index
		int nextPlayer = (currentPlayer + 1) % numberOfPlayers; 
		
		//now handle the different cases by backtracking the different possibilities for making a move:
		if(Utility.isPlayerMrX(player)) {
			//so we're mrX and we gotta win this
			int maximizedScore = Integer.MIN_VALUE;
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
				int heuristicScore = miniMax(depth + 1, nextPlayer, playersLocations, playersTickets);
				if(heuristicScore > maximizedScore) {
					//we've found a better move :)
					maximizedScore = heuristicScore;
					maximizedMove = move;
				}
				//we're done examining this scenario, now revert changes
				//return tickets:
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) + 1); //increase number by 1
				}
				//return to original location
				playersLocations.put(player, currentPlayerLocation);
				currentDepthOtimalMove = maximizedMove; //retain optimal move
			}
		}
		else {
			//we're a cop and therefore ZE ENEMY!!!
			int minimizedScore = Integer.MAX_VALUE;
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
				int heuristicScore = miniMax(depth + 1, nextPlayer, playersLocations, playersTickets);
				if(heuristicScore < minimizedScore) {
					//we've found a better move :)
					minimizedScore = heuristicScore;
					minimizedMove = move;
				}
				//we're done examining this scenario, now revert changes
				//return tickets:
				for(Ticket ticket : necessaryTickets) {
					Map<Ticket, Integer> playerTicketPool = playersTickets.get(player);
					playerTicketPool.put(ticket, playerTicketPool.get(ticket) + 1); //increase number by 1
				}
				//return to original location
				playersLocations.put(player, currentPlayerLocation);
				currentDepthOtimalMove = minimizedMove; //retain optimal move
			}
		}
		if(depth == 0) optimalMove = currentDepthOtimalMove;
		return 0;
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
