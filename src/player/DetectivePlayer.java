package player;
import java.util.*;
import scotlandyard.*;

public class DetectivePlayer extends AIMasterRace {
	public DetectivePlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
	}
	
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		Collections.shuffle(possibleMoves);
		return possibleMoves.get(0);
	}
	
	@Override
	public String toString() {
		return "Player is of class type: DetectivePlayer with colour: " + colour;
	}
}
