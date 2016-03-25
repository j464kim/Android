package ca.uwaterloo.lab2_201_11;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class LinearAccelerationEventListener implements SensorEventListener{
	final int SAMPLING_VALUE = 4;
	final float NOISE_LIMIT = 0.4f;
	LineGraphView graph;
	TextView tv;
	Machine machine;
	float[][] rawValues;
	float[] adjustedValues;
	String dataStream;
	boolean paused;
	int counter;
	
	
	public LinearAccelerationEventListener(TextView tvParam, LineGraphView graphParam, Machine machineParam){
		graph = graphParam;
		machine = machineParam;
		tv = tvParam;
		paused = false;
		rawValues = new float[3][SAMPLING_VALUE];
		counter = 0;
		adjustedValues = new float[3];
		dataStream = "0,";
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if(!paused && se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
			if (counter >= SAMPLING_VALUE) {
				counter = 0;
				adjustedValues = noiseReducer(average(rawValues));
				graph.addPoint(magnitude(adjustedValues));
				machine.setValue(magnitude(adjustedValues)[0]);
				dataStream += Float.toString(magnitude(adjustedValues)[0]) + ", ";
				if(dataStream.length() > 1500){
					dataStream = dataStream.substring(100);
				}
				tv.setText("Current State: " + machine.getState().getName()+
						"\nState Count: \nState1: " +
						Integer.toString(machine.getStateCount()[0]) + " \nState2: " +
						Integer.toString(machine.getStateCount()[1]) + " \nBESTState3: " +
						Integer.toString(machine.getStateCount()[2]) + " \nState4: " +
						Integer.toString(machine.getStateCount()[3]) + " \nState5: " + 
						Integer.toString(machine.getStateCount()[4]) + " \nState6: " +
						Integer.toString(machine.getStateCount()[5]) + " \nSteps: " +
						Integer.toString(machine.getSteps()));
				rawValues = new float[3][SAMPLING_VALUE];
			}
			rawValues[0][counter] = se.values[0];
			rawValues[1][counter] = se.values[1];
			rawValues[2][counter] = se.values[2];
			++counter;
		}
	}
	
	public void reset(){
		dataStream = "";
	}
	
	public String getData(){
		return dataStream;
	}
	
	private float[] magnitude(float[] input){
		float[] output = {0};
		for(int i = 0; i < 3; i++){
			output[0] += input[i] * input[i];
		}
		Math.sqrt(output[0]);
		return output;
	}
	
	private float[] average(float[][] input){
		float[] output = new float[3];
		for(int i = 0; i < SAMPLING_VALUE; i++){
			output[0] += input[0][i];
			output[1] += input[1][i];
			output[2] += input[2][i];
		}
		output[0] /= SAMPLING_VALUE;
		output[1] /= SAMPLING_VALUE;
		output[2] /= SAMPLING_VALUE;
		return output;
	}
	
	private float[] noiseReducer(float[] input){
		float[] output = {1,0,0};
		if(input[1] > NOISE_LIMIT || input[1] < -NOISE_LIMIT){
			output[1] = input[1];
		}
		if(input[2] > NOISE_LIMIT || input[2] < -NOISE_LIMIT){
			output[2] = input[2];
		}
		return output;
	}

	public boolean isPaused(){
		return paused;
	}
	
	public void pause(){
		paused = true;
	}
	
	public void unPause(){
		paused = false;
	}
}
