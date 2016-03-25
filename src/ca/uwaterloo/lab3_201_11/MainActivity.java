package ca.uwaterloo.lab3_201_11;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import android.R.color;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//it is static
	private static LineGraphView graph;
	private static Mapper mv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		graph = new LineGraphView(getApplicationContext(),100, Arrays.asList("x", "y", "z"));
		graph.setVisibility(View.VISIBLE);
		mv = new Mapper(getApplicationContext(), 950, 850, 37, 37);
		MapLoader ml = new MapLoader();
		
		//this is internal memory for my phone, not sd card
		File Root = Environment.getExternalStorageDirectory();
		File Dir = new File(Root.getAbsolutePath()+"/ECE155");
		if(!Dir.exists()){
			Dir.mkdirs();
		}
		
		PedometerMap map = ml.loadMap(Dir, "room.svg");
		mv.setMap(map);
		mv.setBackgroundColor(Color.WHITE);
		registerForContextMenu(mv);
		
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		mv.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		return super.onContextItemSelected(item) || mv.onContextItemSelected(item);
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
		//Reinitialize the paused things, maybe later
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
			LinearAccelerationEventListener linAcceloListener;
			AccelerometerEventListener acceloListener;
			MagneticFieldEventListener magnoListener;
			
			public resetClickListener(Machine mech, LinearAccelerationEventListener l, AccelerometerEventListener a, MagneticFieldEventListener m) {
				machine = mech;
				linAcceloListener = l;
				acceloListener = a;
				magnoListener = m;
			}
			@Override
			public void onClick(View v) {
				graph.purge();
				machine.reset();
				linAcceloListener.reset();
				acceloListener.reset();
				magnoListener.reset();
			}
		}
		
		//the Step button, triggers one step
		class stepClickListener implements OnClickListener{
			int stepCounter;
			TextView stepsView;
			TextView aOutput;
			boolean aExists;
			Machine machine;
			LinearAccelerationEventListener stepCounting;
			LinearAccelerationEventListener linAcceloListener;
			AccelerometerEventListener acceloListener;
			MagneticFieldEventListener magnoListener;
					
			public stepClickListener(Machine mech, LinearAccelerationEventListener l, AccelerometerEventListener a, MagneticFieldEventListener m) {
				machine = mech;
				linAcceloListener = l;
				acceloListener = a;
				magnoListener = m;
			}
			@Override
			public void onClick(View v) {
				stepCounter++;
				machine.getSteps();	
			}
		}
				
		
		//the pause button, pauses all data collection
		class pauseClickListener implements OnClickListener{
			LinearAccelerationEventListener linAcceloListener;
			AccelerometerEventListener acceloListener;
			MagneticFieldEventListener magnoListener;
			
			public pauseClickListener(LinearAccelerationEventListener l, AccelerometerEventListener a, MagneticFieldEventListener m) {
				linAcceloListener = l;
				acceloListener = a;
				magnoListener = m;
			}
			@Override
			public void onClick(View v) {
				if(linAcceloListener.isPaused()){
					linAcceloListener.unPause();
				}else{
					linAcceloListener.pause();
				}
				if(acceloListener.isPaused()){
					acceloListener.unPause();
				}else{
					acceloListener.pause();
				}
				if(magnoListener.isPaused()){
					magnoListener.unPause();
				}else{
					magnoListener.pause();
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
			LinearLayout outer = (LinearLayout)rootView.findViewById(R.id.background);
			linlayout.setBackgroundColor(Color.BLACK);
			outer.setBackgroundColor(Color.BLACK);
			
			//Sensors
			SensorManager sensorManager = (SensorManager)rootView.getContext().getSystemService(SENSOR_SERVICE);
			Sensor linAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
			Sensor magnoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

			//data value labels
			TextView stepsCounter = new TextView(rootView.getContext());
			TextView orientation = new TextView(rootView.getContext());
			TextView displacement = new TextView(rootView.getContext());
			
			displacement.setTextColor(Color.WHITE);
			displacement.setText("None");
			
			orientation.setTextColor(Color.WHITE);
			orientation.setText("No Idea");
			
			stepsCounter.setTextColor(Color.WHITE);
			stepsCounter.setText("None");
			
			
			//Titles
			TextView graphTitle = (TextView) rootView.findViewById(R.id.label1);
			graphTitle.setText("---Linear Acceleration Graph(Total Magnitude)---");
			graphTitle.setTextColor(Color.RED);
			
			TextView stepsTitle = new TextView(rootView.getContext());
			stepsTitle.setText("---Steps---");
			stepsTitle.setTextColor(Color.RED);
			
			TextView displacementTitle = new TextView(rootView.getContext());
			displacementTitle.setText("---Displacement---");
			displacementTitle.setTextColor(Color.RED);
			
			TextView mapTitle = new TextView(rootView.getContext());
			mapTitle.setText("---Map---");
			mapTitle.setTextColor(Color.RED);
			
			//drawing the compass
			ImageView compass = (ImageView)rootView.findViewById(R.id.compass);
			compass.setImageResource(R.drawable.compass);
			compass.setRotation(180);
			
			//magnetic field and accelerometer listeners
			SensorEventListener magnoListener = new MagneticFieldEventListener();
			sensorManager.registerListener(magnoListener, magnoSensor, SensorManager.SENSOR_DELAY_GAME);
			
			SensorEventListener acceloListener = new AccelerometerEventListener(orientation,(MagneticFieldEventListener) magnoListener, compass);
			sensorManager.registerListener(acceloListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
			
			//making the FSM
			Machine machine = new Machine((AccelerometerEventListener)acceloListener);
			
			//Linear acceleration listener
			SensorEventListener linAcceloListener = new LinearAccelerationEventListener(stepsCounter,displacement, graph, machine);
			sensorManager.registerListener(linAcceloListener, linAccelSensor,SensorManager.SENSOR_DELAY_FASTEST);

			//reset button
			Button resetButton = new Button(rootView.getContext());
			resetClickListener resetClick = new resetClickListener(machine, (LinearAccelerationEventListener)linAcceloListener, (AccelerometerEventListener) acceloListener, (MagneticFieldEventListener) magnoListener);
			resetButton.setOnClickListener(resetClick);
			resetButton.setBackgroundColor(Color.BLUE);
			resetButton.setTextColor(Color.WHITE);
			resetButton.setText("Reset");
			
			//pause button
			Button pauseButton = new Button(rootView.getContext());
			pauseClickListener pauseClick = new pauseClickListener((LinearAccelerationEventListener)linAcceloListener, (AccelerometerEventListener) acceloListener, (MagneticFieldEventListener) magnoListener);
			pauseButton.setOnClickListener(pauseClick);
			pauseButton.setBackgroundColor(Color.RED);
			pauseButton.setTextColor(Color.WHITE);
			pauseButton.setText("Pause");
			
			//save button
			Button saveButton = (Button) rootView.findViewById(R.id.save);
			saveButton.setOnClickListener(new saveClickListener((LinearAccelerationEventListener)linAcceloListener));
			
			//step button
			Button stepButton = new Button(rootView.getContext());
			stepClickListener stepClick = new stepClickListener(machine, (LinearAccelerationEventListener)linAcceloListener, (AccelerometerEventListener) acceloListener, (MagneticFieldEventListener) magnoListener);
			stepButton.setOnClickListener(stepClick);
			stepButton.setBackgroundColor(Color.GREEN);
			stepButton.setTextColor(Color.WHITE);
			stepButton.setText("Step");
					
			//adding the Views to the layout
			linlayout.addView(graph);
			//linlayout.addView(stepsTitle);
			linlayout.addView(stepsCounter);
			linlayout.addView(mapTitle);
			linlayout.addView(mv);
			linlayout.addView(displacementTitle);
			linlayout.addView(displacement);
			linlayout.addView(orientation);
			linlayout.addView(pauseButton);
			linlayout.addView(resetButton);
			linlayout.addView(stepButton);
			linlayout.setOrientation(LinearLayout.VERTICAL);
			
			return rootView;
		}
	}
}

