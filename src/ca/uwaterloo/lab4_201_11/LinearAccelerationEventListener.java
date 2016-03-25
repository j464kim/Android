package ca.uwaterloo.lab4_201_11;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.TextView;

//Collects linear acceleration data
//Mostly for counting steps
//now also has the path planner
public class LinearAccelerationEventListener extends PausableListener implements IMapperListener{
	final int SAMPLING_VALUE = 4;
	final float NOISE_LIMIT = 0.4f;
	TextView stepsView;
	TextView displacementView;
	TextView mapInfo;
	Machine machine;
	float[][] rawValues;
	float[] adjustedValues;
	int counter;
	float northSouth = 0;
	float eastWest = 0;
	Mapper mapper;
	List<PointF> path;
	int steps;
	float stepLength;
	Graph graph;
	boolean loaded;
	boolean displaying;
	boolean arrived;
	String text;
	
	
	public LinearAccelerationEventListener(TextView tvSteps, TextView tvDisplacement, TextView map, LineGraphView graphParam, Machine machineParam, Mapper mv){
		super();
		machine = machineParam;
		stepsView = tvSteps;
		displacementView = tvDisplacement;
		mapInfo = map;
		rawValues = new float[3][SAMPLING_VALUE];
		counter = 0;
		adjustedValues = new float[3];
		mapper = mv;
		graph = new Graph();
		steps = 0;
		stepLength = 0.6f;
		path = new ArrayList<PointF>();
		loaded = false;
		displaying = false;
		text = "";
		arrived = false;
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		//data handling
		if(!paused && se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
			if (counter >= SAMPLING_VALUE) {
				counter = 0;
				adjustedValues = noiseReducer(average(rawValues));
				boolean isWall = !mapper.calculateIntersections(new PointF((float) (mapper.getUserPoint().x + stepLength * Math.sin(Math.toRadians(machine.getHeading()))), mapper.getUserPoint().y + stepLength *(float) Math.cos(Math.toRadians(machine.getHeading()))), mapper.getUserPoint()).isEmpty();
				machine.setWall(isWall);
				machine.setValue(magnitude(adjustedValues)[0]);
				//updates the map
				int temp = steps;
				steps = machine.getSteps();
				if(temp != steps){
					mapper.setUserPoint(mapper.getUserPoint().x + stepLength*(machine.goEast()), mapper.getUserPoint().y - stepLength*(machine.goNorth()));
					path = graph.findPath(mapper.getUserPoint(), mapper.getEndPoint());
					mapper.setUserPath(path);
					graph.reset();
				}
				text = "Step Length: " + stepLength + "m.";
				if(!arrived){
					if(!path.isEmpty()){
						text += "\nTotal Distance Left: " + getDistance(path) + "m";
					}
				}
				if(FloatHelper.distance(mapper.getUserPoint(), mapper.getEndPoint()) < 1){
					text += "\nYou Have Arrived!";
					arrived = true;
				}else{
					arrived = false;
				}
				if(isWall){
					text += "\nStatus: Hitting Wall!";
				}else{
					text += "\nStatus: All Clear";
				}
				mapInfo.setText(text);
				displacementView.setText("North: " + Float.toString(machine.getNorth()) + "\nEast: " + Float.toString(machine.getEast()));
				rawValues = new float[3][SAMPLING_VALUE];
			}
			rawValues[0][counter] = se.values[0];
			rawValues[1][counter] = se.values[1];
			rawValues[2][counter] = se.values[2];
			++counter;
		}
	}
	
	private float getDistance(List<PointF> path){
		float distance = 0;
		for(int i = 1; i < path.size(); ++i){
			distance += FloatHelper.distance(path.get(i-1), path.get(i));
		}
		return distance;
	}
	
	public void setStepLength(float length){
		stepLength = length;
	}
	
	public float getStepLength(){
		return stepLength;
	}
	
	//resets the whole thing
	public void reset(){
		path = new ArrayList<PointF>();
		mapper.removeAllLabeledPoints();
		graph.hardReset();
		loaded = false;
		mapper.setUserPoint(mapper.getStartPoint());
	}
	
	//gets the total magnitide of the directional data
	private float[] magnitude(float[] input){
		float[] output = {0};
		for(int i = 0; i < 3; i++){
			output[0] += input[i] * input[i];
		}
		Math.sqrt(output[0]);
		return output;
	}
	
	//obvious
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
	
	//ignores small changes
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
	
	//the waypoint pointer angle offset
	public float getPointerAngle(){
		if(path != null){
			if(path.size() >= 3){
				if((Math.toDegrees(FloatHelper.angleBetween(path.get(path.size()-1), path.get(path.size()-3), new PointF(0, mapper.getUserPoint().y))))<90){
					return(360 - (float)Math.toDegrees(FloatHelper.angleBetween(path.get(path.size()-1), path.get(path.size()-3), new PointF(mapper.getUserPoint().x, 0))));
				}else{			
					return (float)Math.toDegrees(FloatHelper.angleBetween(path.get(path.size()-1), path.get(path.size()-3), new PointF(mapper.getUserPoint().x, 0)));
				}
			}
		}
		return 0;
	}
	
	//makes all nodes visible
	public void toggleGrid(){
		if(!loaded){//loads the map if havent already
			graph.addMap(mapper);
			loaded = true;
		}
		if(!displaying){
			displaying = true;
			int count = 0;
			for(Node n: graph.getNodes()){
				PointF p = new PointF(n.getX(), n.getY());
				mapper.addLabeledPoint(p, Integer.toString(count));
				++count;
			}
		}else{
			displaying = false;
			mapper.removeAllLabeledPoints();
		}
	}
	
	@Override
	public void locationChanged(Mapper source, PointF loc) {
		source.setUserPoint(loc);//sets the userpoint to be the start point
		if(!loaded){//loads the map if havent already
			graph.addMap(source);
			loaded = true;
		}
		//calculates new path
		path = graph.findPath(loc, source.getEndPoint());
		source.setUserPath(path);
		graph.reset();
	}
	
	@Override
	public void DestinationChanged(Mapper source, PointF dest) {
		if(!loaded){//loads the map if havent already
			graph.addMap(source);
			loaded = true;
		}
		//calculates new path
		path = graph.findPath(source.getStartPoint(), source.getEndPoint());
		source.setUserPath(path);
		graph.reset();
	}
}
