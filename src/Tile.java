import java.util.List;
import java.util.ArrayList;

public class Tile {
    private int baseResources;
    private int numResources;
    private int refreshTime = 3;
    private List<Agent> occupants = new ArrayList<Agent>();
    private int lastUpdated = 0;

    public Tile(int baseResources){
	this.baseResources = baseResources;
	this.numResources = baseResources;
    }

    public int getResources(){
	return numResources;
    }

    public int harvest(int amount){
	if (amount > numResources){
	    numResources = 0;
	    return numResources;
	}
	numResources -= amount;
	return amount;
    }

    public void update(int tick){
	if (tick - lastUpdated > refreshTime){
	    numResources = baseResources;
	}
    }

    public void addOccupant(Agent occupant){
	occupants.add(occupant);
    }

    public List<Agent> getOccupants(){
	return occupants;
    }
}
