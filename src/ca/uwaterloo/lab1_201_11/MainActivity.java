package ca.uwaterloo.lab1_201_11;

import java.util.Arrays;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	//yes, I know it is static
	static LineGraphView graph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x", "y", "z"));
		graph.setVisibility(View.VISIBLE);
		//I made graph static and added it to the layout in the fragment,
		//I get errors when I try to add it to the layout here
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onPause(){
		super.onPause();
		//pause the stuff, maybe later
	}
	
	protected void onResume(){
		super.onResume();
		//Reinitialize the paused things, later
	}	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
		
		//Light sensor listener
		class LightSensorEventListener implements SensorEventListener{

			TextView output;
			float[] maxValue;
			
			public LightSensorEventListener(TextView outputView, float[] max){
				output = outputView;
				maxValue = max;
			}
			public void onAccuracyChanged(Sensor s, int i) {}
			
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
					//max value conditions
					if(se.values[0] > maxValue[0]){
						maxValue[0] = se.values[0];
					}
					output.setText(new String (String.format("Light Sensor Value: %n Current: %n %.1f %n Max: %n %.1f", se.values[0], maxValue[0])));						
				}
			}
		}
		
		//Accelerometer listener
		class AccelerationSensorEventListener implements SensorEventListener{
			TextView output;
			float[] maxValues;
			
			public AccelerationSensorEventListener(TextView outputView, float[] max){
				output = outputView;
				maxValues = max;
			}
			public void onAccuracyChanged(Sensor s, int i) {}
			
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					//max value conditions
					if(Math.abs(se.values[0]) > Math.abs(maxValues[0])){
						maxValues[0] = se.values[0];
					}
					if(Math.abs(se.values[1]) > Math.abs(maxValues[1])){
						maxValues[1] = se.values[1];
					}
					if(Math.abs(se.values[2]) > Math.abs(maxValues[2])){
						maxValues[2] = se.values[2];
					}
					graph.addPoint(se.values);
					output.setText(new String (String.format("Accelerometer Values:%n Current: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", se.values[0],se.values[1],se.values[2], maxValues[0], maxValues[1], maxValues[2])));										
				}
			}
		}
		
		//Magnetic field listener
		class MagneticFieldSensorEventListener implements SensorEventListener {
			TextView output;
			float[] maxValues;
			
			public MagneticFieldSensorEventListener(TextView outputView, float[] max){
				output = outputView;
				maxValues = max;
			}
			public void onAccuracyChanged(Sensor s, int i) {}
			
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
					//max value conditions
					if(Math.abs(se.values[0]) > Math.abs(maxValues[0])){
						maxValues[0] = se.values[0];
					}
					if(Math.abs(se.values[1]) > Math.abs(maxValues[1])){
						maxValues[1] = se.values[1];
					}
					if(Math.abs(se.values[2]) > Math.abs(maxValues[2])){
						maxValues[2] = se.values[2];
					}
					output.setText(new String (String.format("Magnetic Field Sensor Values:%n Current: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", se.values[0],se.values[1],se.values[2], maxValues[0], maxValues[1], maxValues[2])));										
				}
			}
		}
		
		//Rotation vector listener
		class RotationVectorSensorEventListener implements SensorEventListener {
			TextView output;
			float[] maxValues;
			
			public RotationVectorSensorEventListener(TextView outputView, float[] max){
				output = outputView;
				maxValues = max;
			}
			
			public void onAccuracyChanged(Sensor s, int i) {}
			
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
					//max value conditions
					if(Math.abs(se.values[0]) > Math.abs(maxValues[0])){
						maxValues[0] = se.values[0];
					}
					if(Math.abs(se.values[1]) > Math.abs(maxValues[1])){
						maxValues[1] = se.values[1];
					}
					if(Math.abs(se.values[2]) > Math.abs(maxValues[2])){
						maxValues[2] = se.values[2];
					}
					output.setText(new String (String.format("Rotational Vector Sensor Values:%n Current: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", se.values[0],se.values[1],se.values[2], maxValues[0], maxValues[1], maxValues[2])));										
				}
			}
		}
		
		//the reset button listener, pass by value almost got me
		class buttonClickListener implements OnClickListener{
			TextView lOutput;
			TextView aOutput;
			TextView mOutput;
			TextView rOutput;
			boolean lExists;
			boolean aExists;
			boolean mExists;
			boolean rExists;
			float[] maxLight = {0,0,0};
			float[] maxAccel = {0,0,0};
			float[] maxMagno = {0,0,0};
			float[] maxRotato = {0,0,0};
			
			public buttonClickListener(TextView tvl, TextView tva, TextView tvm, TextView tvr, 
										boolean l, boolean a, boolean m, boolean r,
										float[] lmax, float[] amax, float[] mmax, float[] rmax) {
				lOutput = tvl;
				aOutput = tva;
				mOutput = tvm;
				rOutput = tvr;
				lExists = l;
				aExists = a;
				mExists = m;
				rExists = r;
				maxLight = lmax;
				maxAccel = amax;
				maxMagno = mmax;
				maxRotato = rmax;
			}
			
			@Override
			public void onClick(View v) {
				maxLight[0] = 0;
				maxAccel[0] = 0;
				maxMagno[0] = 0;
				maxRotato[0] = 0;
				maxAccel[1] = 0;
				maxMagno[1] = 0;
				maxRotato[1] = 0;
				maxAccel[2] = 0;
				maxMagno[2] = 0;
				maxRotato[2] = 0;
				//updating the text values as the reset button is pressed
				//does not make a difference for good devices, but for some cheaper devices, 
				//the sensor's onSensorChanged() may not be called often enough to change the text
				if(lExists)lOutput.setText(new String (String.format("Light Sensor Value: %n Current: %n Unchanged %n Max: %n %.1f", maxLight[0])));
				if(aExists)aOutput.setText(new String (String.format("Accelerometer Values:%n Current: %n x-axis: Unchanged %n y-axis: Unchanged %n z-axis: Unchanged %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", maxAccel[0], maxAccel[1], maxAccel[2])));										
				if(mExists)mOutput.setText(new String (String.format("Magnetic Field Sensor Values:%n Current: %n x-axis: Unchanged %n y-axis: Unchanged %n z-axis: Unchanged %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", maxMagno[0], maxMagno[1], maxMagno[2])));										
				if(rExists)rOutput.setText(new String (String.format("Rotational Vector Sensor Values:%n Current: %n x-axis: Unchanged %n y-axis: Unchanged %n z-axis: Unchanged %n Max: %n x-axis: %.1f %n y-axis: %.1f %n z-axis: %.1f", maxRotato[0], maxRotato[1], maxRotato[2])));										
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			//Main View and layout
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			LinearLayout linlayout = (LinearLayout)rootView.findViewById(R.id.layout);
			
			//Sensors
			SensorManager sensorManager = (SensorManager)rootView.getContext().getSystemService(SENSOR_SERVICE);
			Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			Sensor magnoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			Sensor rotatoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			
			//Sensor Availability flags
			final boolean lightExists = (lightSensor != null);
			final boolean accelExists = (accelSensor != null);
			final boolean magnoExists = (magnoSensor != null);
			final boolean rotatoExists = (rotatoSensor != null);
			
			//Sensor Availability labels
			TextView availableTitle = (TextView) rootView.findViewById(R.id.label1);
			TextView lightCheck = new TextView(rootView.getContext());
			TextView accelCheck = new TextView(rootView.getContext());
			TextView magnoCheck = new TextView(rootView.getContext());
			TextView rotatoCheck = new TextView(rootView.getContext());
			TextView dataTitle = new TextView(rootView.getContext());
			if (lightExists){
				lightCheck.setText("Light Sensor Available");
		    } else {
		    	lightCheck.setText("No Light Sensor");
		    }
			if (accelExists){
				accelCheck.setText("Accelerometer Available");
		    } else {
		    	accelCheck.setText("No Accelerometer");
		    }
			if (magnoExists){
				magnoCheck.setText("Magnetic Field Sensor Available");
		    } else {
		    	magnoCheck.setText("No Magnetic Field Sensor");
		    }
			if (rotatoExists){
				rotatoCheck.setText("Rotational Vector Sensor Available \n");
		    } else {
		    	rotatoCheck.setText("No Rotational Vector Sensor \n");
		    }
			
			//data value labels and max values
			float[] maxLight = {0};
			float[] maxAccel = {0,0,0};
			float[] maxMagno = {0,0,0};
			float[] maxRotato = {0,0,0};
			TextView lightValue = new TextView(rootView.getContext());
			TextView accelValue = new TextView(rootView.getContext());
			TextView magnoValue = new TextView(rootView.getContext());
			TextView rotatoValue = new TextView(rootView.getContext());
			
			//default text
			availableTitle.setText("---Sensor Availability---");
			availableTitle.setTextColor(Color.RED);
			dataTitle.setText("---Data---");
			dataTitle.setTextColor(Color.RED);
			lightValue.setText("Light Sensor Value: \n N/A");
			accelValue.setText("Accelerometer Values: \n N/A");
			magnoValue.setText("Magnetic Field Sensor Values: \n N/A");
			rotatoValue.setText("Rotational Vector Sensor Values: \n N/A");
			
			//max reset button
			Button button = new Button(rootView.getContext());
			buttonClickListener click = new buttonClickListener(lightValue, accelValue, magnoValue, rotatoValue, 
											lightExists, accelExists, magnoExists, rotatoExists,
											maxLight, maxAccel, maxMagno, maxRotato);
			button.setOnClickListener(click);
			button.setBackgroundColor(Color.BLUE);
			button.setTextColor(Color.WHITE);
			button.setText("Reset");
			
			//making the listeners and registering them if they exist on the device
			if(lightExists){
				SensorEventListener lightListener = new LightSensorEventListener(lightValue, maxLight);
				sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			if(magnoExists){
				SensorEventListener magnoListener = new MagneticFieldSensorEventListener(magnoValue, maxMagno);
				sensorManager.registerListener(magnoListener, magnoSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			if(accelExists){
				SensorEventListener acceloListener = new AccelerationSensorEventListener(accelValue, maxAccel);
				sensorManager.registerListener(acceloListener, accelSensor,SensorManager.SENSOR_DELAY_NORMAL);
			}
			if(rotatoExists){
				SensorEventListener rotatoListener = new RotationVectorSensorEventListener(rotatoValue, maxRotato);
				sensorManager.registerListener(rotatoListener, rotatoSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			//adding the Views to the layout
			linlayout.addView(lightCheck);
			linlayout.addView(accelCheck);
			linlayout.addView(magnoCheck);
			linlayout.addView(rotatoCheck);
			linlayout.addView(graph);
			linlayout.addView(dataTitle);
			linlayout.addView(lightValue);
			linlayout.addView(accelValue);
			linlayout.addView(magnoValue);
			linlayout.addView(rotatoValue);
			linlayout.addView(button);
			linlayout.setOrientation(LinearLayout.VERTICAL);
			
			return rootView;
		}
	}
}

