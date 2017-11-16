
Android Navigaton implementing A* path-finding algorithm

Commit 0:
				Initial Commit.

Commit 1:
				1. Added items to the user interface for an Android application.
				2. Created event handlers for a variety of sensors and made them modify the user interface.
				3. Included a widget to graph a history of sensor values.

Commit 2:
				1. Examined accelerometer readings to identify patterns.
				2. Implemented more sophisticated event handlers for the accelerometer sensor.
				3. Filtered raw sensor data to account for noise and bias.
				4. Designed and implemented a pedometer algorithm.

Commit 3:
				1. Filtered raw sensor data to account for noise and bias.
				2. Raw rotation readings to identify patterns.
				3. Combined sensor readings to track a user’s displacement.
				4. Loaded the map.

Commit 4:
				1. Tracked the user’s position on a model of the physical world.
				2. Accounted for errors in sensor readings based on knowledge of the physical world.
				3. Implemented a A* path-finding algorithm which can effectively guide a user to a destination with the shorted path.

