package ca.uwaterloo.lab3_201_11;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MagneticFieldEventListener implements SensorEventListener{
	float[] values;
	boolean paused;
	
	
	public MagneticFieldEventListener(){
		paused = false;
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
