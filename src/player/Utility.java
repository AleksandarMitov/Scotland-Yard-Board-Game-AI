package player;
import java.util.*;
import scotlandyard.*;

public class Utility {
	private static final Colour mrXColour = Colour.Black;
	
	static boolean isPlayerMrX(Colour player) {
		return mrXColour.equals(player); 
	}
	
	static boolean isPlayerDetective(Colour player) {
		return !isPlayerMrX(player);
	}
	
	static Colour getMrXColour() {
		return mrXColour;
	}
	
	/**
	 * Returns a list of the necessary tickets to perform a move
	 * @param move
	 * @return
	 */
	static List<Ticket> getNecessaryTickets(Move move) {
		List<Ticket> result = new ArrayList<Ticket>();
		if(move instanceof MoveTicket) {
			Ticket ticket = ((MoveTicket) move).ticket;
			result.add(ticket);
		}
		else if(move instanceof MoveDouble) {
			Ticket ticket1 = ((MoveDouble) move).move1.ticket;
			Ticket ticket2 = ((MoveDouble) move).move2.ticket;
			result.add(ticket1);
			result.add(ticket2);
		}
		return result;
	}
	
	/**
	 * Returns the end locations of a move
	 * @param currentLocation
	 * @param move
	 * @return
	 */
	static int getMoveEndLocation(int currentLocation, Move move) {
		if(move instanceof MovePass) return currentLocation;
		else if(move instanceof MoveTicket) return ((MoveTicket) move).target;
		else if(move instanceof MoveDouble) return ((MoveDouble) move).move2.target;
		else return -1;
	}
}
