package ca.uwaterloo.lab3_201_11;

public class Machine {

	State state;
	float prevValue;
	float peakValue;
	int time;
	int stepCounter;
	float north;
	float east;
	int[] stateCounter;
	AccelerometerEventListener acceloListener;
	
	public Machine(AccelerometerEventListener a){
		state = State.PEAK1;
		stepCounter = 0;
		prevValue = 0;
		north = 0;
		east = 0;
		time = 0;
		acceloListener = a;
		stateCounter = new int[]{0,0,0,0,0,0};
	}
	
	public void reset(){
		stepCounter = 0;
		stateCounter = new int[]{0,0,0,0,0,0};
		north = 0;
		east = 0;
	}
	
	public State getState(){
		return state;
	}
	
	public float getNorth(){
		return north;
	}
	
	public float getEast(){
		return east;
	}
	
	public int getSteps(){
		return stepCounter;
	}
	
	
	public int[] getStateCount(){
		return stateCounter;
	}
	
	public void setValue(float input){
		//The core of the finite state machine,
		//Uses a switch statement to select the current state
		switch(state){
			case PEAK1:
				//Initial state, goes to next state if the graph decreases above a threshold
				//Basically a local maximum within a certain range triggers next state
				if(prevValue >= 7 && input < prevValue){
					stateCounter[0]++;
					if(prevValue <= 45 && input < prevValue){
						stateCounter[1]++;
						state = State.DROP1;
						peakValue = prevValue;
						time = 0;
					}
				}
				break;
			case DROP1:
				//A local minimum within a certain range within a time interval triggers next state
				if(time < 8){
					if(prevValue <= 7 && input >= prevValue && time >= 3){
						state = State.PEAK2;
						stateCounter[2]++;
						stepCounter++;
						time = 0;
						float heading = acceloListener.getHeading();
						north += Math.cos(Math.toRadians(heading));
						east += Math.sin(Math.toRadians(heading));
					}else if(input >= prevValue){
						state = State.PEAK1;
					}
				} else {
					state = State.PEAK1;
				}
				++time;
				break;
			case PEAK2:
				//A local maximum within a certain range within a time interval triggers next state
				if(time < 10){
					if(prevValue <= peakValue*0.8 && input <= prevValue){
						state = State.DROP2;
						stateCounter[3]++;
						peakValue = prevValue;
						time = 0;
					}
				}else{
					state = State.PEAK1;
				}
				++time;
				break;
				
			//it turns out anything past here is too strict, just three states for this fsm is enough
			case DROP2:
				//A local minimum within a certain range within a time interval triggers next state
				if(time < 10){
					if(prevValue <= peakValue*0.7 && input >= prevValue){
						state = State.PEAK3;
						stateCounter[4]++;
						time = 0;
					}else if(input >= prevValue){
						state = State.PEAK1;
						stateCounter[0]++;
					}
				}else{
					state = State.PEAK1;
					stateCounter[0]++;
				}
				++time;
				break;
			case PEAK3:
				//A local maximum within a certain range within a time interval triggers next state
				if(time < 12){
					if(prevValue < peakValue && input <= prevValue){
						state = State.PEAK1;
						stateCounter[5]++;
						time = 0;
					}
				}else{
					state = State.PEAK1;
					stateCounter[0]++;
				}
				++time;
				break;
		}
		prevValue = input;
	}
	
}
