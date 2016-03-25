package ca.uwaterloo.lab4_201_11;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
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
		
		//the reset button listener
		//resets all values
		class resetClickListener implements OnClickListener{
			TextView aOutput;
			boolean aExists;
			Machine machine;
			PausableListener[] listeners;
			
			public resetClickListener(Machine mech, PausableListener[] l) {
				machine = mech;
				listeners = l;

			}
			
			@Override
			public void onClick(View v) {
				graph.purge();
				machine.reset();
				for(PausableListener l: listeners){
					l.reset();
				}
			}
		}
		
		//the Step button, triggers one step
		class stepClickListener implements OnClickListener{
			Machine machine;
					
			public stepClickListener(Machine mech) {
				machine = mech;
			}
			@Override
			public void onClick(View v) {
				machine.addSteps();	
			}
		}
		
		//the display button, displays nodes
		class displayNodesListener implements OnClickListener{
			LinearAccelerationEventListener linAccel;
			public displayNodesListener(LinearAccelerationEventListener l) {
				linAccel = l;
			}
			@Override
			public void onClick(View v) {
				linAccel.toggleGrid();
			}
		}
		
		//the submit button, triggers one step
		class submitListener implements OnClickListener{
			LinearAccelerationEventListener linAccel;
			EditText text;
			public submitListener(LinearAccelerationEventListener l, EditText et) {
				linAccel = l;
				text = et;
			}
			@Override
			public void onClick(View v) {
				((LinearAccelerationEventListener)linAccel).setStepLength(Float.parseFloat(text.getText().toString()));
				Toast.makeText(getActivity(), "Step length set to: " + linAccel.getStepLength() + "m.", Toast.LENGTH_LONG).show();
			}
		}
		
		//the pause button, pauses all data collection
		class pauseClickListener implements OnClickListener{
			PausableListener[] listener;
			Button button;
			public pauseClickListener(PausableListener[] l, Button b) {
				listener = l;
				button = b;
			}
			@Override
			public void onClick(View v) {
				for(PausableListener l : listener){
					if(l.isPaused()){
						button.setBackgroundColor(Color.RED);
						button.setText("Pause");
						l.unPause();
					}else{
						button.setBackgroundColor(Color.GREEN);
						button.setText("Unpause");
						l.pause();
					}
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
			Sensor linAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
			Sensor magnoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

			//data value labels
			TextView stepsCounter = new TextView(rootView.getContext());
			TextView orientation = new TextView(rootView.getContext());
			TextView displacement = new TextView(rootView.getContext());
			TextView mapData = new TextView(rootView.getContext());
			
			displacement.setTextColor(Color.WHITE);
			displacement.setText("None");
			
			orientation.setTextColor(Color.WHITE);
			orientation.setText("No Idea");
			
			stepsCounter.setTextColor(Color.WHITE);
			stepsCounter.setText("None");
			
			mapData.setTextColor(Color.WHITE);
			
			//Titles
			TextView stepsTitle = new TextView(rootView.getContext());
			stepsTitle.setText("---Steps---");
			stepsTitle.setTextColor(Color.RED);
			
			TextView displacementTitle = new TextView(rootView.getContext());
			displacementTitle.setText("---Compass---");
			displacementTitle.setTextColor(Color.RED);
			
			TextView mapTitle = new TextView(rootView.getContext());
			mapTitle.setText("---Map---");
			mapTitle.setTextColor(Color.RED);
			
			//drawing the compass
			ImageView compass = new ImageView(rootView.getContext());
			compass.setImageResource(R.drawable.compass);
			compass.setRotation(180);
			
			//drawing the direction heading
			ImageView pointer = new ImageView(rootView.getContext());
			pointer.setImageResource(R.drawable.pointer);
			pointer.setRotation(180);
			
			//magnetic field listener
			PausableListener magnoListener = new MagneticFieldEventListener();
			sensorManager.registerListener(magnoListener, magnoSensor, SensorManager.SENSOR_DELAY_GAME);
			
			//making the FSM
			Machine machine = new Machine();
			
			//Linear acceleration listener
			PausableListener linAcceloListener = new LinearAccelerationEventListener(stepsCounter,displacement,mapData, graph, machine, mv);
			sensorManager.registerListener(linAcceloListener, linAccelSensor,SensorManager.SENSOR_DELAY_FASTEST);
			
			PausableListener acceloListener = new AccelerometerEventListener(orientation,(MagneticFieldEventListener) magnoListener, (LinearAccelerationEventListener)linAcceloListener, compass, pointer);
			sensorManager.registerListener(acceloListener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
			//making the FSM
			machine.addListener((AccelerometerEventListener)acceloListener);
			
			//An array of all of the sensor listeners
			PausableListener[] allListeners = {linAcceloListener, acceloListener, magnoListener};
			
			//reset button
			Button resetButton = new Button(rootView.getContext());
			resetClickListener resetClick = new resetClickListener(machine, allListeners);
			resetButton.setOnClickListener(resetClick);
			resetButton.setBackgroundColor(Color.BLUE);
			resetButton.setTextColor(Color.WHITE);
			resetButton.setText("Reset");
			
			//pause button
			Button pauseButton = new Button(rootView.getContext());
			pauseClickListener pauseClick = new pauseClickListener(allListeners, pauseButton);
			pauseButton.setOnClickListener(pauseClick);
			pauseButton.setBackgroundColor(Color.RED);
			pauseButton.setTextColor(Color.WHITE);
			pauseButton.setText("Pause");
			
			//step button
			Button stepButton = new Button(rootView.getContext());
			stepButton.setOnClickListener(new stepClickListener(machine));
			stepButton.setBackgroundColor(Color.MAGENTA);
			stepButton.setTextColor(Color.BLACK);
			stepButton.setText("Step");
			
			//display button
			Button dButton = new Button(rootView.getContext());
			dButton.setOnClickListener(new displayNodesListener((LinearAccelerationEventListener) linAcceloListener));
			dButton.setBackgroundColor(Color.LTGRAY);
			dButton.setTextColor(Color.BLACK);
			dButton.setText("Show Nodes");
			
			//edit text for step length
			EditText sLength = new EditText(rootView.getContext());
			sLength.setTextColor(Color.WHITE);
			sLength.setHint("Enter step length in m...");
			Button submit = new Button(rootView.getContext());
			submit.setText("Submit Length");
			submitListener s = new submitListener((LinearAccelerationEventListener)linAcceloListener, sLength);
			submit.setOnClickListener(s);
			
			//Mapping stuff
			mv.addListener((IMapperListener) linAcceloListener);
			
			//adding the Views to the layout
			LinearLayout buttonLay = new LinearLayout(rootView.getContext());
			buttonLay.setBackgroundColor(Color.BLACK);
			buttonLay.setGravity(Gravity.CENTER);
			buttonLay.addView(dButton);
			buttonLay.addView(pauseButton);
			buttonLay.addView(resetButton);
			buttonLay.addView(stepButton);
			
			linlayout.addView(sLength);
			linlayout.addView(submit);
			linlayout.addView(buttonLay);
			linlayout.addView(mapData);
			linlayout.addView(orientation);
			linlayout.addView(mapTitle);
			linlayout.addView(mv);
			linlayout.addView(displacementTitle);
			
			RelativeLayout compassLay = new RelativeLayout(rootView.getContext());
			compassLay.setBackgroundColor(Color.BLACK);
			compassLay.setGravity(Gravity.CENTER);
			compassLay.addView(compass);
			compassLay.addView(pointer);

			
			linlayout.addView(compassLay);
			linlayout.setOrientation(LinearLayout.VERTICAL);
			
			return rootView;
		}
	}
}

