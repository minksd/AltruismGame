import java.util.List;

public class Agent {
    private double resources;
    private List<Choice> actions;

    public Choice getChoice(int tick){
	return Choice.War;
    }

    public void updateResource(double amount){
    }
}
