package ca.uwaterloo.lab3_201_11;

public enum State {
	PEAK1("Peak1"),
	DROP1("Drop1"),
	PEAK2("Peak2"),
	DROP2("Drop2"),
	PEAK3("Peak3"),
	DROP3("DROP3");
	
	private final String name;
	State(String title){
		name = title;
	}
	public String getName(){
		return name;
	}
}
