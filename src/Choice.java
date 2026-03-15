public enum Choice {
    War("War"),
    Peace("Peace"),
    ;

    private String name;
    Choice(String name){
	this.name = name;
    }
    
    @Override
    public String toString(){
	return this.name;
    }
}
