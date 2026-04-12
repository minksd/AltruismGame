import java.util.Random;
import java.util.ArrayList;

public class GeneticAlgorithm {
    private int numGenerations = 10000;
    private int mutationRate = 3;
    private int numAgents = 100;
    private int numTicks = 100;
    private Agent[] agents;
    private Random rand = new Random();
    private double[] distHist = new double[numGenerations];
    private double[] bestHist = new double[numGenerations];
    private boolean distributeInitialChoice = true;

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.createAgents();
        ga.optimize();
        ga.printResults(10);
    }

    private Choice getChoiceFromDouble(double choice, double choiceNum, int agentNum) {
        if (distributeInitialChoice) {
            choice = distributeInitialChoice(choiceNum, ((double) agentNum) / ((double) numAgents), .3);
        }
        if (Math.round(choice) == 0) {
            return Choice.War;
        } else {
            return Choice.Peace;
        }
    }

    private double distributeInitialChoice(double choiceNum, double lowerBound, double spread) {
        if (choiceNum / numTicks < lowerBound) {
            return 0;
        } else if (choiceNum / numTicks > lowerBound + spread) {
            return 0;
        } else {
            return 1;
        }
    }

    private void createAgents() {
        agents = new Agent[numAgents];

        for (int agentNum = 0; agentNum < numAgents; agentNum++) {
            Choice[] choices = new Choice[numTicks];

            for (int tick = 0; tick < numTicks; tick++) {
                choices[tick] = getChoiceFromDouble(rand.nextDouble(), tick, agentNum);
            }
            agents[agentNum] = new Agent(choices);
        }
    }

    private void buildGame() {
        Game game = new Game(agents, numTicks);
        game.runGame();
    }

    private void optimize() {
        for (int curGen = 1; curGen <= numGenerations; curGen++) {
            System.out.println("\nCurrent Generation: " + curGen + "\n");
            buildGame();
            // System.out.println("Average Dist: \n" + getAvgDist() + "%");
            distHist[curGen - 1] = getAvgDist();
            // System.out.println("Best Agent: \n" + getBestAgent().getPercentageWar() + "%
            // : " + getBestAgent().getResources());
            bestHist[curGen - 1] = getBestAgent().getResources();
            agents = mutate(breed(select(agents)));
        }

    }

    private Agent[] select(Agent[] agents) {
        Agent[] parents = { agents[0], agents[1] };
        for (int i = 2; i < agents.length; i++) {
            if (agents[i].getResources() > parents[0].getResources()) {
                parents[1] = parents[0];
                parents[0] = agents[i];
            } else if (agents[i].getResources() > parents[1].getResources()) {
                parents[1] = agents[i];
            }
        }
        return parents;
    }

    private Agent breed(Agent[] agents) {
        Choice[] actions = new Choice[agents[0].getActions().length];
        for (int i = 0; i < actions.length; i++) {
            if (rand.nextBoolean()) {
                actions[i] = agents[0].getChoice(i);
            } else {
                actions[i] = agents[1].getChoice(i);
            }
        }
        return new Agent(actions);
    }

    private Agent[] mutate(Agent agent) {
        Agent[] newAgents = new Agent[numAgents];
        for (int i = 0; i < numAgents; i++) {
            Choice[] actions = new Choice[agent.getActions().length];
            for (int j = 0; j < actions.length; j++) {
                if (rand.nextInt(0, 100) > mutationRate) {
                    actions[j] = agent.getChoice(j);
                } else {
                    actions[j] = (agent.getChoice(j) == Choice.War) ? Choice.Peace : Choice.War;
                }
            }
            newAgents[i] = new Agent(actions);
        }
        return newAgents;
    }

    private Agent getBestAgent() {
        Agent bestAgent = agents[0];
        for (Agent agent : agents) {
            if (agent.getResources() > bestAgent.getResources()) {
                bestAgent = agent;
            }
        }
        return bestAgent;
    }

    private double getAvgDist() {
        double total = 0;
        for (Agent agent : agents) {
            total += agent.getPercentageWar();
        }
        return total / numAgents;
    }

    public void printResults(int numSections) {
        ArrayList<Double> distSections = new ArrayList<Double>();
        ArrayList<Double> bestSections = new ArrayList<Double>();
        for (int curSection = 1; curSection <= numSections; curSection++) {
            distSections.add(distHist[(int) ((numGenerations - 1) * ((double) curSection / numSections))]);
            bestSections.add(bestHist[(int) ((numGenerations - 1) * ((double) curSection / numSections))]);
        }
        System.out.println("Average % War over time (sampled): " + distSections);
        System.out.println("Best Agent Resources over time (sampled): " + bestSections);
    }
}
