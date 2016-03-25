package ca.uwaterloo.lab4_201_11;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

//Collects magnetic field data
public class MagneticFieldEventListener extends PausableListener{
	float[] values;
	
	
	public MagneticFieldEventListener(){
		super();
		values = new float[3];
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if(!paused && se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			values[0] = se.values[0];
			values[1] = se.values[1];
			values[2] = se.values[2];
		}
	}
	
	public void reset(){
		values = new float[3];
	}
	
	public float[] getValues(){
		return values;
	}
}
