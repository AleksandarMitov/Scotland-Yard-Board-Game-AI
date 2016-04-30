package player;

import net.*;
import scotlandyard.*;

import java.io.IOException;
import java.util.*;

/**
 * The AIMasterRaceFactory is an example of a PlayerFactory that
 * gives the AI server your AI implementation. You can also put any
 * code that you want to run before and after a game in the methods
 * provided here.
 */
public class AIMasterRaceFactory implements PlayerFactory {

	private final Colour mrXColour = Colour.Black; //making things more modular and stuff
	private List<Spectator> aiPlayers = new ArrayList<Spectator>();
	
    @Override
    public Player getPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
    	AIMasterRace newPlayer;
    	if(isMrX(colour)) newPlayer = new MrXPlayer(colour, view, mapFilename);
    	else newPlayer = new DetectivePlayer(colour, view, mapFilename);
    	aiPlayers.add(newPlayer);
        return newPlayer;
    }

    @Override
    public void ready() {
        System.out.println("Ready.... Fight!");
    }

    @Override
    public List<Spectator> getSpectators(ScotlandYardView view) {
    	return aiPlayers;
    }

    @Override
    public void finish() {
        aiPlayers.clear();
    }
    
    private boolean isMrX(Colour colour) {
    	return colour.equals(mrXColour);
    }

}
