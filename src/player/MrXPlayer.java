package player;
import java.util.*;
import scotlandyard.*;

public class MrXPlayer extends AIMasterRace {
	private final int depthToSimulate = 10;
	
	public MrXPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
		super(colour, view, mapFilename);
	}
	
	@Override
	protected Move chooseMove(int currentLocation, List<Move> possibleMoves) {
		Collections.shuffle(possibleMoves);
		return possibleMoves.get(0);
	}
	
	@Override
	public String toString() {
		return "Player is of class type: MrXPlayer with colour: " + colour;
	}
}
