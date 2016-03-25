package ca.uwaterloo.lab4_201_11;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class PausableListener implements SensorEventListener {
	protected boolean paused;
	
	public PausableListener(){
		paused = false;
	}
	
	public abstract void onAccuracyChanged(Sensor s, int i);
	
	public abstract void onSensorChanged(SensorEvent se);
	
	abstract void reset();
	
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
