package player;
import java.util.*;
import scotlandyard.*;

public class Utility {
	/**
	 * Holds the colour of mr.X
	 */
	private static final Colour mrXColour = Colour.Black;
	
	/**
	 * Checks if a player is mr.X or not
	 * @param player
	 * @return true is player is mr.X, false otherwise
	 */
	static boolean isPlayerMrX(Colour player) {
		return mrXColour.equals(player); 
	}
	
	/**
	 * Checks if a player is a detective or not
	 * @param player
	 * @return true is player is a detective, false otherwise
	 */
	static boolean isPlayerDetective(Colour player) {
		return !isPlayerMrX(player);
	}
	
	/**
	 * Gives the colour of mr.X
	 * @return the colour of mr.X
	 */
	static Colour getMrXColour() {
		return mrXColour;
	}
	
	/**
	 * Returns a list of the necessary tickets to perform a move
	 * @param move the move we want to perform
	 * @return a list of the tickets necessary to perform the move
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
	 * @return the target location of a move
	 */
	static int getMoveEndLocation(int currentLocation, Move move) {
		if(move instanceof MovePass) return currentLocation;
		else if(move instanceof MoveTicket) return ((MoveTicket) move).target;
		else if(move instanceof MoveDouble) return ((MoveDouble) move).move2.target;
		else return -1;
	}
}
