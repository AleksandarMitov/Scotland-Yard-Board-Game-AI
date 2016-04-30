package player;

import scotlandyard.*;
import java.util.*;
import java.io.*;

/**
 * The AIMasterRace is the ultimate AI with an attitude that
 * makes a glorious move from the given set of moves. Since the
 * AIMasterRace implements Player, the only required method is
 * notify(), which takes the location of the player and the
 * list of valid moves. The return value is the desired move,
 * which must be one from the list.
 */
public abstract class AIMasterRace implements Player, Spectator {

	protected ScotlandYardView view;
	protected ScotlandYardGraph graph; //holds game graph
	protected Colour colour;
	
    public AIMasterRace(Colour playerColour, ScotlandYardView view, String graphFilename){
    	this.view = view;
    	this.graph = generateGameGraph(graphFilename);
    	this.colour = playerColour;
    	System.out.println("Creating player: " + colour);
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        System.out.println("Preparing to make a move");
        
        //Start messing around
        List<Colour> players = view.getPlayers();
        System.out.println(this.toString());
        if(colour.equals(Colour.Blue)) {
        	for(Colour player : players)
            {
            	System.out.println("Player " + player + "'s position is: " + view.getPlayerLocation(player));
            }
        }
        //End messing around
        
        Move moveToPlay = chooseMove(location, moves);
        System.out.println("Playing move: " + moveToPlay);
        receiver.playMove(moveToPlay, token);
    }
    
    @Override
    public void notify(Move move) {
    	if(move instanceof MovePass) {
    		
    	}
    	else if(move instanceof MoveTicket) {
    		
    	}
    	else if(move instanceof MoveDouble) {
    		
    	}
    	else {
    		
    	}
    	System.out.println("Notifying player" + colour + "for move: " + move);
    }
    
    private ScotlandYardGraph generateGameGraph(String graphFileName) {
    	ScotlandYardGraphReader reader = new ScotlandYardGraphReader();
    	ScotlandYardGraph graph = null;
    	try {
    		graph = reader.readGraph(graphFileName);
    	} catch(IOException e) {
    		System.out.println("IOException while trying to read graph from file: " + graphFileName);
    		System.out.println(e.getMessage());
    	}
    	return graph;
    }
    
    /**
     * This method returns the move we're going to play which we later send to the client
     * @return move we're going to play
     */
    protected abstract Move chooseMove(int currentLocation, List<Move> possibleMoves);
}
