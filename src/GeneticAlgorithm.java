import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneticAlgorithm {
    private int numGenerations = 10;
    private int generationSize = 5;
    private int mutationRate = 1;
    private int numAgents = 10;
    private int numTicks = 1000;
    private List<Agent> agents;

	public static void main(String[] args) {
		GeneticAlgorithm ga = new GeneticAlgorithm();
		Agent[] agents = new Agent[ga.numAgents];

		for (int agentNum = 0; agentNum < ga.numAgents; agentNum++) {
		    Choice[] choices = (new java.util.Random())
			.doubles(ga.numTicks)
			.mapToObj(ga::getChoiceFromInt)
			.collect(Collectors.toList()).toArray(new Choice[ga.numTicks]);
		    
		    Agent agent = new Agent(choices);
		    agents[agentNum] = agent;
		}
		Game game = new Game(agents, ga.numTicks);
		game.runGame();
	}

	public Choice getChoiceFromInt(double choice) {
	    if (Math.round(choice) == 0) {
			return Choice.War;
		} else {
			return Choice.Peace;
		}
	}
    private void createAgents(){}
    private void buildGame(){}
    private void runGame(){}
    private void optimize(){}
    private Agent[] select(Agent[] agents){return new Agent[0];}
    private Agent[] breed(Agent[] agents){return new Agent[0];}
    private Agent[] mutate(Agent[] agents){return new Agent[0];}
}
