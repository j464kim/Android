package ca.uwaterloo.lab2_201_11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.uwaterloo.lab2_201_11.R;

public class MainActivity extends Activity {
	//it is static
	private static LineGraphView graph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		//FrameLayout mainlayout = (FrameLayout)findViewById(R.id.container);
		graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x", "y", "z"));
		//mainlayout.setBackgroundColor(Color.BLACK);
		//mainlayout.addView(graph);
		graph.setVisibility(View.VISIBLE);
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
		
		//the reset button listener, pass by value almost got me
		class resetClickListener implements OnClickListener{
			TextView aOutput;
			boolean aExists;
			Machine machine;
			LinearAccelerationEventListener acceloListener;
			
			public resetClickListener(Machine m, LinearAccelerationEventListener l) {
				machine = m;
				acceloListener = l;
			}
			@Override
			public void onClick(View v) {
				graph.purge();
				machine.reset();
				acceloListener.reset();
			}
		}
		
		//the pause button, pauses the graph
		class pauseClickListener implements OnClickListener{
			LinearAccelerationEventListener accelListener;
			public pauseClickListener(LinearAccelerationEventListener l) {
				accelListener = l;
			}
			@Override
			public void onClick(View v) {
				if(accelListener.isPaused()){
					accelListener.unPause();
				}else{
					accelListener.pause();
				}
			}
		}
		//the save button, save a csv file with all the graph data
		class saveClickListener implements OnClickListener{
			LinearAccelerationEventListener accelListener;
			public saveClickListener(LinearAccelerationEventListener l) {
				accelListener = l;
			}
			@Override
			public void onClick(View v) {
				String state = Environment.getExternalStorageState();
				if(Environment.MEDIA_MOUNTED.equals(state)){
					File Root = Environment.getExternalStorageDirectory();
					File Dir = new File(Root.getAbsolutePath()+"/ECE155");
					if(!Dir.exists()){
						Dir.mkdirs();
					}
					File file = new File(Dir, "data.csv");
					try{
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(accelListener.getData().getBytes());
						fos.close();
						Toast.makeText(getActivity(), "saved", Toast.LENGTH_LONG).show();
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getActivity(), "No SD Card", Toast.LENGTH_LONG).show();
				}
			}
		}
			
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			//Main View and layout
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			LinearLayout linlayout = (LinearLayout)rootView.findViewById(R.id.layout);
			linlayout.setBackgroundColor(Color.BLACK);
			
			//Sensors
			SensorManager sensorManager = (SensorManager)rootView.getContext().getSystemService(SENSOR_SERVICE);
			Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

			//Sensor Availability flags
			final boolean accelExists = (accelSensor != null);
			
			//Sensor Availability labels
			TextView availableTitle = (TextView) rootView.findViewById(R.id.label1);
			availableTitle.setTextColor(Color.WHITE);
			TextView accelCheck = new TextView(rootView.getContext());
			accelCheck.setTextColor(Color.WHITE);
			TextView dataTitle = new TextView(rootView.getContext());
			dataTitle.setTextColor(Color.WHITE);
			if (accelExists){
				accelCheck.setText("Linear Acceleration Available");
		    } else {
		    	accelCheck.setText("No Linear Acceleration");
		    }
			
			//data value labels and max values
			TextView stepsCounter = new TextView(rootView.getContext());
			stepsCounter.setTextColor(Color.WHITE);
			stepsCounter.setText("None");
			
			//default text
			availableTitle.setText("---Sensor Availability---");
			availableTitle.setTextColor(Color.RED);
			dataTitle.setText("---Steps---");
			dataTitle.setTextColor(Color.RED);
			
			//making the FSM
			Machine machine = new Machine();
			
			//making the listeners and registering them if they exist on the device
			SensorEventListener acceloListener = new LinearAccelerationEventListener(stepsCounter, graph, machine);
				sensorManager.registerListener(acceloListener, accelSensor,SensorManager.SENSOR_DELAY_FASTEST);
			//reset button
			Button resetButton = new Button(rootView.getContext());
			resetClickListener resetClick = new resetClickListener(machine, (LinearAccelerationEventListener)acceloListener);
			resetButton.setOnClickListener(resetClick);
			resetButton.setBackgroundColor(Color.BLUE);
			resetButton.setTextColor(Color.WHITE);
			resetButton.setText("Reset");
			//pause button
			Button pauseButton = new Button(rootView.getContext());
			pauseClickListener pauseClick = new pauseClickListener((LinearAccelerationEventListener)acceloListener);
			pauseButton.setOnClickListener(pauseClick);
			pauseButton.setBackgroundColor(Color.RED);
			pauseButton.setTextColor(Color.WHITE);
			pauseButton.setText("Pause");
			
			//save button
			Button saveButton = (Button) rootView.findViewById(R.id.save);
			saveButton.setOnClickListener(new saveClickListener((LinearAccelerationEventListener)acceloListener));

			//adding the Views to the layout
			linlayout.addView(accelCheck);
			linlayout.addView(graph);
			linlayout.addView(dataTitle);
			linlayout.addView(stepsCounter);
			linlayout.addView(pauseButton);
			linlayout.addView(resetButton);
			linlayout.setOrientation(LinearLayout.VERTICAL);
			
			return rootView;
		}
	}
}

