public class Agent {
	private double resources = 0;
	private Choice[] actions;

	public Agent(Choice[] actions) {
		this.actions = actions;
	}

	public Choice getChoice(int tick) {
		return actions[tick];
	}

	public void updateResource(double amount) {
		resources += amount;
	}

	public double getResources() {
		return resources;
	}

	public int getPercentageWar() {
		int numWar = 0;
		for (Choice choice : actions) {
			if (choice == Choice.War) {
				numWar++;
			}
		}
		if (numWar == 0)
			return 0;
		return (int) Math.round(((float) numWar / (float) actions.length) * 100);
	}

	public Choice[] getActions(){
		return actions;
	}
}
