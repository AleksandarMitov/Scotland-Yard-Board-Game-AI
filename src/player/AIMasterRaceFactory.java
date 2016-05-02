package player;

import net.*;
import scotlandyard.*;
import java.util.*;

/**
 * The AIMasterRaceFactory is an example of a PlayerFactory that
 * gives the AI server your AI implementation. You can also put any
 * code that you want to run before and after a game in the methods
 * provided here.
 */
public class AIMasterRaceFactory implements PlayerFactory {
	private List<Spectator> spectators = new ArrayList<Spectator>();
	
    @Override
    public Player getPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
    	AIMasterRace newPlayer;
    	//if player is mr.X, create a MrXPlayer instance
    	//if player is a detective, create a DetectivePlayer instance
    	if(Utility.isPlayerMrX(colour)) newPlayer = new MrXPlayer(colour, view, mapFilename);
    	else newPlayer = new DetectivePlayer(colour, view, mapFilename);
    	//spectators.add(newPlayer); //add player as a spectator
        return newPlayer;
    }

    @Override
    public void ready() {
        System.out.println("Ready.... Fight!");
    }

    @Override
    public List<Spectator> getSpectators(ScotlandYardView view) {
    	return spectators;
    }

    @Override
    public void finish() {
        spectators.clear();
    }

}
