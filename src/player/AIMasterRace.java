package player;

import scotlandyard.*;
import java.util.*;

import graph.Edge;

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
	
	/**
	 * Holds current game map's graph
	 */
	protected ScotlandYardGraph graph;
	
	/**
	 * The colour of this player
	 */
	protected Colour colour;
	
    public AIMasterRace(Colour playerColour, ScotlandYardView view, String graphFilename){
    	this.view = view;
    	this.graph = generateGameGraph(graphFilename);
    	this.colour = playerColour;
    	System.out.println("Spawning AI player: " + colour);
    }

    /**
     * Implementing the player interface
     */
    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        System.out.println("It's " + colour + " player's turn");
    	System.out.println("Flexing AI muscles to come up with a move...");
        Move moveToPlay = chooseMove(location, moves);
        System.out.println("Piece of cake! Current location: " + location + ", Playing move: " + moveToPlay);
        receiver.playMove(moveToPlay, token);
    }
    
    /**
     * Implementing the Spectator interface...
     */
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
    
    /**
     * Reads the given graph file and returns a ScotlandYardGraph instance populated with it
     * @param graphFileName
     * @return
     */
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
    
    /**
     * Returns a map with keys being the players and their locations as respective values
     * @return a (Colour, Integer) map
     */
    protected Map<Colour, Integer> getPlayersLocations() {
    	Map<Colour, Integer> locations = new HashMap<Colour, Integer>();
    	for(Colour player : view.getPlayers()) {
    		locations.put(player, view.getPlayerLocation(player));
    	}
    	return locations;
    }
	/**
     * Returns a map associating a ticket count to a ticket type for given player
     * @param player
     * @return map of the number of tickets of each type the given player has
     */
    protected Map<Ticket, Integer> getPlayerTickets(Colour player) {
    	Map<Ticket, Integer> result = new HashMap<Ticket, Integer>();
    	for(Ticket ticket : Ticket.values()) {
    		int count = view.getPlayerTickets(player, ticket);
    		if(count > 0) result.put(ticket, count);
    	}
    	return result;
    }
    
    /**
     * Returns associates a map of tickets to each player
     * @param view
     * @return
     */
    protected Map<Colour, Map<Ticket, Integer>> getPlayersTickets() {
    	Map<Colour, Map<Ticket, Integer>> playersTickets = new HashMap<Colour, Map<Ticket, Integer>>();
    	for(Colour player : view.getPlayers()) {
    		Map<Ticket, Integer> ticketsForCurrentPlayer = getPlayerTickets(player);
    		playersTickets.put(player, ticketsForCurrentPlayer);
    	}
    	return playersTickets;
    }
    
    /**
	 * Generates all possible moves with a single ticket for the player
	 * @param player the player that has to make the move
	 * @param location the location the player is starting from
	 * @param forbiddenLocations the locations which the player explicitly cannot go to
	 * @return a list containing all the legal moves
	 */
    protected List<Move> generateMoves(Colour playerColour, Map<Colour, Integer> playersLocations, Map<Colour, Map<Ticket, Integer>> playersTickets) {
    	List<Move> possibleMoves = new ArrayList<Move>();
    	Set<Move> movesSet = new HashSet<Move>(); //use it to make the search for redundant tickets faster
    	int playerLocation = playersLocations.get(playerColour);
    	List<Edge<Integer, Transport>> edges = graph.getEdgesFrom(graph.getNode(playerLocation)); //getting all the edges starting from the given location
    	
    	//generate forbidden locations - the ones with a detective on them
    	Set<Integer> forbiddenLocations = new HashSet<Integer>(); //will hold the locations of the detectives
    	for(Colour player : playersLocations.keySet()) {
    		if(Utility.isPlayerDetective(player)) {
    			int currentPlayerLocation = playersLocations.get(player);
    			forbiddenLocations.add(currentPlayerLocation); //adding detective's location as forbidden
    		}
    	}
    	if(edges == null) return possibleMoves;//getEdges from returns null when there are no edges so we gotta handle that case
    	
    	//see if each edge represents a legal move for the player
    	for(Edge<Integer, Transport> edge : edges)
    	{
    		Transport whatKindOfTransport = edge.getData();//What kind of transport we must use following this edge
    		Ticket whatKindOfTicket = Ticket.fromTransport(whatKindOfTransport);//What kind of ticket we must use
    		Integer endLocation = edge.getTarget().getIndex(); //the end location of the ticket
    		MoveTicket moveTicket = MoveTicket.instance(playerColour, whatKindOfTicket, endLocation);//end result
    		//if we got that kind of ticket and location is not forbidden, we can make the move
    		Integer playerTicketsOfKind = playersTickets.get(playerColour).get(whatKindOfTicket);
    		boolean validityCriteria = playerTicketsOfKind != null && playerTicketsOfKind > 0 && !forbiddenLocations.contains(endLocation);
    		//avoiding redundant tickets
    		if(validityCriteria && !movesSet.contains(moveTicket)) {
    			possibleMoves.add(moveTicket);
    			movesSet.add(moveTicket);
    		}
    	}
    	/*
    	 Now, if the player is MrX and has a black ticket, then he can use it
    	 to go to any of the locations he can using other kinds of tickets
    	 so we should generate those possibilities as well.
    	 */
    	Integer secretTicketCount = playersTickets.get(playerColour).get(Ticket.Secret);
    	if(Utility.isPlayerMrX(playerColour) && secretTicketCount != null && secretTicketCount > 0)
    	{
    		List<MoveTicket> secretMoves = new ArrayList<MoveTicket>();
    		for(Move move : possibleMoves)
    		{
    			//Skip the moves by boat which already require a secret ticket
    			if(((MoveTicket) move).ticket != Ticket.Secret)
    			{
    				secretMoves.add(MoveTicket.instance(playerColour, Ticket.Secret, ((MoveTicket) move).target));
    			}
    		}
    		//merge the two lists
    		possibleMoves.addAll(secretMoves);
    	}
    	//now if a detective has no legal moves, we add a MovePass ticket
    	if(Utility.isPlayerDetective(playerColour) && possibleMoves.isEmpty()) possibleMoves.add(MovePass.instance(playerColour));
        return possibleMoves;
    }
    
    /**
     * Returns a list of all the players on the map while also
     * making sure they're in their correct order
     * was forced to make one since the ScotlandYardView implementation was buggy
     * @param view
     * @return a list of all the players in their correct order
     */
    protected List<Colour> getPlayersInOrder(ScotlandYardView view) {
    	List<Colour> inOrder = new ArrayList<Colour>();
    	inOrder.add(Utility.getMrXColour());
    	for(Colour player : view.getPlayers()) {
			if(Utility.isPlayerDetective(player)) inOrder.add(player);
		}
    	return inOrder;
    }
}
