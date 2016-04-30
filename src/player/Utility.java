package player;
import java.util.*;
import scotlandyard.*;

public class Utility {
	private static final Colour mrXColour = Colour.Black;
	
	public static boolean isPlayerMrX(Colour player) {
		return mrXColour.equals(player); 
	}
	
	public static boolean isPlayerDetective(Colour player) {
		return !isPlayerMrX(player);
	}
	public static Colour getMrXColour() {
		return mrXColour;
	}
	
}
