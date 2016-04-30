package player;
import java.util.*;
import scotlandyard.*;

public class MrXPlayer extends AIMasterRace {
	private final int depthToSimulate = 10;
	private final int numberOfPlayers;
	public MrXPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
		numberOfPlayers = view.getPlayers().size();
	}
	
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		Map<Colour, Integer> playersLocations = getPlayersLocations();
		//we're mrX so we should update with our true location that only we know every round
		playersLocations.put(view.getCurrentPlayer(), currentLocation);
		Map<Colour, Map<Ticket, Integer>> playersTickets = getPlayersTickets();
		List<Move> legalMoves = generateMoves(view.getCurrentPlayer(), playersLocations, playersTickets);
		
		//messing around
		List<Move> filtered = new ArrayList<Move>();
		for(Move move : possibleMoves)
		{
			if(move instanceof MoveDouble)
			{
				MoveDouble m = (MoveDouble) move;
				if(!m.move1.ticket.equals(m.move2.ticket)) filtered.add(move);
			}
		}
		possibleMoves = filtered;
		//now being serious again
		
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
	
	private int maximizeMrXWinPotential(int depth, int currentPlayer, boolean shouldMrXWin, Map<Colour, Integer> playersLocations, Map<Colour, Map<Ticket, Integer>> playersTickets) {
		if(currentPlayer == 0 && depth == depthToSimulate*numberOfPlayers) return evaluateState(playersLocations, graph); //we've reached the depth, now apply heuristic
		
		Colour player = view.getPlayers().get(currentPlayer);
		List<Move> legalMoves = generateMoves(player, playersLocations, playersTickets);
		//TODO: FINISH IT
		return 0;
	}
	
	/**
	 * Heuristically evaluate the game state
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
