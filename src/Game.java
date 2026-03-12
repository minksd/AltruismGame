import java.util.List;
import java.util.HashMap;
import java.util.Random;

public class Game{
    private int maxHarvestable = 5;
    private Tile[][] grid;
    private int currentTick;
    private int maxTick;
    private Agent[] agents;

    public Game(Agent[] agents, int maxTick){
	this.maxTick = maxTick;
	this.agents = agents;

	int numAgents = agents.length;
	Tile[][] grid = new Tile[numAgents][numAgents];
	Random rand = new Random();
	for (int i = 0; i < numAgents; i++){
	    for (int j = 0; j < numAgents; j++){
		//Logarithmic Rarity of high starting resources, averages out to 5 over the 0 to 20 range.
		//The log can return less than 0 for input 0, so max between 0 and the output should clamp to between 0 and 20.
		grid[i][j] = new Tile(Math.max(0, Math.round( (float) Math.log(2.3025 * ((double) rand.nextInt(0,21)) + 0.01f) + 3)));
	    }
	}
	this.grid = grid;
    }
    
    public void runGame(){
	while (++currentTick < maxTick){
	    updateTiles();
	    moveAgents();
	    HashMap<Agent, Float> outcomes = calcOutcomes();
	    distributeResources(outcomes);
	}
	for (Agent agent: agents){
	    System.out.println(agent.getPercentageWar() + "% : " + agent.getResources());
	}
    }
    
    private void updateTiles(){
	for (Tile[] row: grid){
	    for (Tile tile: row){
		tile.update(currentTick);
	    }
	}
    }
    
    private void moveAgents(){
	Random rand = new Random();
	for (Agent agent: agents){
	    int xPosition = Math.round(rand.nextFloat() * (grid[0].length - 1));
	    int yPosition = Math.round(rand.nextFloat() * (grid.length - 1));

	    grid[yPosition][xPosition].addOccupant(agent);
	}
    }
    
    private HashMap<Agent, Float> calcOutcomes(){
	HashMap<Agent, Float> outcomes = new HashMap<Agent, Float>();
	for (Tile[] row: grid){
	    for (Tile tile: row){
		HashMap<Agent,Float> outcome = calcOutcome(tile);
		outcomes.putAll(outcome);
	    }
	}
	return outcomes;
    }
    
    private HashMap<Agent, Float> calcOutcome(Tile tile){
	int numWar = 0;
	int numPeace = 0;
	HashMap<Agent, Float> outcome = new HashMap<Agent, Float>();
	float availableResources = tile.getResources();
	
	for (Agent agent: tile.getOccupants()){
	    Choice choice = agent.getChoice(currentTick);
	    if (choice == Choice.War) numWar++;
	    if (choice == Choice.Peace) numPeace++;
	}
	if (numWar == 0){
	    for (Agent agent: tile.getOccupants()){
		outcome.put(agent,availableResources/tile.getOccupants().size());
	    }
	}
	else {
	    for (Agent agent: tile.getOccupants()){
		if (agent.getChoice(currentTick) == Choice.War){
		    outcome.put(agent, (availableResources/(2*numWar)));
		}
		else {
		    outcome.put(agent, 0f);
		}
	    }
	}
	return outcome;
    } 
    private void distributeResources(HashMap<Agent, Float> outcomes){
	for (Agent agent: outcomes.keySet()){
	    agent.updateResource(outcomes.get(agent));
	}
    }
}
