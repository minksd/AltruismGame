import java.util.HashMap;
import java.util.Random;

public class Game {
	private int maxHarvestable = 5;
	private Tile[][] grid;
	private int currentTick = 0;
	private int maxTick;
	private Agent[] agents;

	public Game(Agent[] agents, int maxTick) {
		this.maxTick = maxTick;
		this.agents = agents;

		int numAgents = agents.length;
		Tile[][] grid = new Tile[numAgents][numAgents];
		Random rand = new Random();
		for (int i = 0; i < numAgents; i++) {
			for (int j = 0; j < numAgents; j++) {
				// Logarithmic Rarity of high starting resources, averages out to 5 over the 0
				// to 20 range.
				// The log can return less than 0 for input 0, so max between 0 and the output
				// should clamp to between 0 and 20.
				grid[i][j] = new Tile(
						Math.max(0, Math.round((float) Math.log(2.3025 * ((double) rand.nextInt(0, 21)) + 0.01f) + 3)));
			}
		}
		this.grid = grid;
	}

	public void runGame() {
		while (currentTick < maxTick) {
/* 			System.out.println("\nCurrent Tick: " + currentTick + '\n');
			for (Agent agent : agents) {
				System.out.println(agent.getResources());
			} */
			updateTiles();
			clearTiles();
			moveAgents();
			HashMap<Agent, Float> outcomes = calcOutcomes();
			distributeResources(outcomes);
			currentTick++;
		}

		for (Agent agent : agents) {
			System.out.println('\n' + agent.getPercentageWar() + "% : " + agent.getResources());
		}
	}

	private void updateTiles() {
		for (Tile[] row : grid) {
			for (Tile tile : row) {
				tile.update(currentTick);
			}
		}
	}

	private void moveAgents() {
		Random rand = new Random();
		for (Agent agent : agents) {
			int xPosition = Math.round(rand.nextFloat() * (grid[0].length - 1));
			int yPosition = Math.round(rand.nextFloat() * (grid.length - 1));

			grid[yPosition][xPosition].addOccupant(agent);
		}
	}

	private HashMap<Agent, Float> calcOutcomes() {
		HashMap<Agent, Float> outcomes = new HashMap<Agent, Float>();
		for (Tile[] row : grid) {
			for (Tile tile : row) {
				HashMap<Agent, Float> outcome = calcOutcome(tile);
				outcomes.putAll(outcome);
			}
		}
		return outcomes;
	}

	private HashMap<Agent, Float> calcOutcome(Tile tile) {
		HashMap<Agent, Float> outcome = new HashMap<>();

		int occupants = tile.getOccupants().size();
		if (occupants == 0) {
			return outcome;
		}

		int numWar = 0;
		for (Agent agent : tile.getOccupants()) {
			if (agent.getChoice(currentTick) == Choice.War) {
				numWar++;
			}
		}

		float availableResources = tile.getResources();

		float totalHarvest = Math.min(availableResources, occupants * maxHarvestable);

		if (numWar == 0) {
			float rewardPerAgent = totalHarvest / occupants;

			for (Agent agent : tile.getOccupants()) {
				outcome.put(agent, rewardPerAgent);
			}
		} else {
			float totalReward = totalHarvest / 2.0f;
			float rewardPerWarAgent = totalReward / numWar;

			for (Agent agent : tile.getOccupants()) {
				if (agent.getChoice(currentTick) == Choice.War) {
					outcome.put(agent, rewardPerWarAgent);
				} else {
					outcome.put(agent, 0f);
				}
			}
		}

		tile.harvest(totalHarvest);
		return outcome;
	}

	private void distributeResources(HashMap<Agent, Float> outcomes) {
		for (Agent agent : outcomes.keySet()) {
			agent.updateResource(outcomes.get(agent));
		}
	}

	private void clearTiles() {
		for (Tile[] row : grid) {
			for (Tile tile : row) {
				tile.clearOccupants();
			}
		}
	}
}
