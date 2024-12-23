package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.*;
import java.awt.*;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}

	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 1;
	private Point homePoint = new Point(1, 1); //Saves the home point to be able to find home later
	private Random random_generator = new Random();

	private Stack<Point> path = new Stack<>(); //Creates a path to the target
	
	// Here you can define your variables!
	public int iterationCounter = 10000;
	public MyAgentState state = new MyAgentState();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(3);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}

	//Decides in which direction the agent should turn to face the target direction
	public Action decideTurn(int direction) {
		int rotation = (direction - state.agent_direction + 4) % 4;

		if (rotation == 1) {
			state.agent_direction = (state.agent_direction + 1) % 4;
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} else if (rotation == 3) {
			state.agent_direction = (state.agent_direction -1 + 4) % 4;
			state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		}
		state.agent_direction = (state.agent_direction + 1) % 4;
		state.agent_last_action = state.ACTION_TURN_RIGHT;
		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	}

	//Decides the action the agent should do to go to the target point
	public Action decideAction(Point p) {
		int pd = -1; //Point direction, used to check in which direction the target point is
		if (p.x > state.agent_x_position) {
			pd = MyAgentState.EAST;
		}
		else if (p.x < state.agent_x_position) {
			pd = MyAgentState.WEST;
		}
		else if (p.y > state.agent_y_position) {
			pd = MyAgentState.SOUTH;
		}
		else if (p.y < state.agent_y_position) {
			pd = MyAgentState.NORTH;
		}
		if (pd == state.agent_direction) {
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			path.pop(); //pops the point because we have moved towards it
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}
		return decideTurn(pd);
	}
	//A bfs to found all unknown points and find the way home after all point are cleared
	public void BFS (boolean findHome) {
		Queue<Point> queue = new LinkedList<>();

		Point[][] previousPoint = new Point[30][30]; //A Point[][] for all previous points, so we can backtrack
		Set<Point> visited = new HashSet<>(); //A set for all visited points
		Point startPoint = new Point(state.agent_x_position, state.agent_y_position); //The point we search from
		Point targetPoint = new Point(); //The point we want to search
		boolean foundTarget = false; //True if we find a target point
		queue.add(startPoint);
		while(!queue.isEmpty()){
			Point current = queue.remove();

			if (state.world[current.x][current.y] == state.UNKNOWN && !findHome) {
				targetPoint.setLocation(current);
				foundTarget = true;
				break;
			} else if (findHome && current.equals(homePoint)) {
				targetPoint.setLocation(current);
				foundTarget = true;
				break;
			}

			for(Point neighbour : getNeighbours(current)){
				int neighbourState = state.world[neighbour.x][neighbour.y];
				if(neighbourState != state.WALL && visited.add(neighbour)){
					queue.add(neighbour);
					previousPoint[neighbour.x][neighbour.y] = current;
				}
			}
		}

		if(foundTarget){
			Point temp = targetPoint;
			while(!temp.equals(startPoint)) {
				path.add(temp);
				temp = previousPoint[temp.x][temp.y];
			}
		}

	}

	//Gets the neighbours for the current point
	public Point[] getNeighbours(Point p) {
		int x = p.x;
		int y = p.y;
		Point[] neighbours = new Point[4];
		neighbours[0] = new Point(x-1, y);
		neighbours[1] = new Point(x, y-1);
		neighbours[2] = new Point(x+1, y);
		neighbours[3] = new Point(x, y+1);
		return neighbours;
	}
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
		if (home)
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.HOME);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
	    
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    }
	    else
	    {
			if(path.isEmpty()) {
				BFS(false); //Find the unknown points
				if (path.isEmpty())
					if (home) {
						System.out.println("All cleared, found home!");
						return NoOpAction.NO_OP;
					}
					else
						BFS(true); //Find the home point
			}
			System.out.println(path.toString());
		}
		return decideAction(path.peek());
    }
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
