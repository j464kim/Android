package ca.uwaterloo.lab3_201_11;

import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class AccelerometerEventListener implements SensorEventListener{
	final int SAMPLING_VALUE = 20;
	final float NOISE_LIMIT = 0.4f;
	float[][] rawValues;
	float[] values;
	float angle;
	float currentAngle;
	boolean paused;
	TextView tv;
	ImageView image;
	MagneticFieldEventListener magnoListener;
	float[] r;
	float[] i;
	int counter;
	int randomCounter;
	Random rng;
	
	public AccelerometerEventListener(TextView textView, MagneticFieldEventListener ml, ImageView img){
		paused = false;
		values = new float[3];
		rawValues = new float[3][SAMPLING_VALUE];
		tv = textView;
		magnoListener = ml;
		r = new float[9];
		i = new float[9];
		currentAngle = 0f;
		angle = 0f;
		counter = 0;
		randomCounter = 0;
		image = img;
		rng = new Random(System.nanoTime());
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if(!paused && se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			if (counter >= SAMPLING_VALUE) {
				counter = 0;
				values[0] = se.values[0];
				values[1] = se.values[1];
				values[2] = se.values[2];
				
				SensorManager.getRotationMatrix(r, i, values, ((MagneticFieldEventListener)magnoListener).getValues());
				SensorManager.getOrientation(r, values);
				values[0] = toDegrees(values[0]);
				values[1] = toDegrees(values[1]);
				values[2] = toDegrees(values[2]);
				tv.setText("Heading: " + Float.toString(((values[0]) + 180) % 360));
				RotateAnimation ra = new RotateAnimation(currentAngle, -values[0], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				ra.setDuration(50);
				ra.setFillAfter(true);
				image.startAnimation(ra);
				currentAngle = -values[0];
				if(randomCounter >= rng.nextInt(60)){
					randomCounter = 0;
					angle = (values[0] + 180) % 360;
				}
			}
			++randomCounter;
			++counter;
		}
	}
	public float getHeading(){
		return angle;
	}
	public void reset(){
		values[0] = 0;
		currentAngle = 0;
		RotateAnimation ra = new RotateAnimation(currentAngle, -values[0], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(80);
		ra.setFillAfter(true);
		image.startAnimation(ra);
	}
	
	private float toDegrees(float radians){
		return (Math.round(180 * radians / Math.PI) + 180) % 360;
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
