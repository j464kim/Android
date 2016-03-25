package ca.uwaterloo.lab4_201_11;

import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

//Collects accelerometer data
//Works in combination with the magnetic field event listener to collect data for the compass
public class AccelerometerEventListener extends PausableListener{
	final int SAMPLING_VALUE = 20;
	final float NOISE_LIMIT = 0.4f;
	float[][] rawValues;
	float[] values;
	float angle;
	float currentAngle;
	TextView tv;
	ImageView compass;
	ImageView pointer;
	MagneticFieldEventListener magnoListener;
	LinearAccelerationEventListener linaccelListener;
	float[] r;
	float[] i;
	int counter;
	int randomCounter;
	Random rng;
	
	public AccelerometerEventListener(TextView textView, MagneticFieldEventListener ml, LinearAccelerationEventListener la, ImageView cps, ImageView pt){
		super();
		values = new float[3];
		rawValues = new float[3][SAMPLING_VALUE];
		tv = textView;
		magnoListener = ml;
		linaccelListener = la;
		r = new float[9];
		i = new float[9];
		currentAngle = 0f;
		angle = 0f;
		counter = 0;
		randomCounter = 0;
		compass = cps;
		pointer = pt;
		rng = new Random(System.nanoTime());
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if(!paused && se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			//Counter is used to slow down and average out the sampled data
			if (counter >= SAMPLING_VALUE) {
				counter = 0;
				
				//stores the sensor values
				//values[0] = values[0] + 0.05f * (se.values[0] - values[0]);
				values[0] = se.values[0];
				values[1] = se.values[1];
				values[2] = se.values[2];
				
				//get orientation values
				SensorManager.getRotationMatrix(r, i, values, ((MagneticFieldEventListener)magnoListener).getValues());
				SensorManager.getOrientation(r, values);
				
				//converts from radians to degrees
				values[0] = toDegrees(values[0]);
				values[1] = toDegrees(values[1]);
				values[2] = toDegrees(values[2]);
				
				//pointer offset
				float offset = linaccelListener.getPointerAngle();
				if(Float.isNaN(linaccelListener.getPointerAngle())){
					offset = 0;
				}
				
				//Writes orientation value onto textfield
				String text = "Current Heading: " + Float.toString(((values[0]) + 180) % 360) + "\nTarget Heading: " + Float.toString((offset) % 360);
				tv.setText(text);
				
				//draws the compass
				if(values[0] < 355 && values[0] > 5){
					RotateAnimation rac = new RotateAnimation(currentAngle, -values[0], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					rac.setDuration(50);
					rac.setFillAfter(true);
					compass.startAnimation(rac);
				}
				currentAngle = -values[0];
				
				
				//draw the pointer
				RotateAnimation rap = new RotateAnimation(currentAngle + offset, -values[0] + offset, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				rap.setDuration(50);
				rap.setFillAfter(true);
				pointer.startAnimation(rap);
				
				//randomizes the sampling time during the step
				if(randomCounter >= rng.nextInt(200)){
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
		compass.startAnimation(ra);
	}
	
	private float toDegrees(float radians){
		return (Math.round(180 * radians / Math.PI) + 180) % 360;
	}
}
