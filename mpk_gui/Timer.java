package mpk_gui;

/** This class is used to control the real-time speed of the animation */
public class Timer {

	/** How much to slow down for slow motion viewing */
	public static final double SLOW_MOTION_RATE = 5.0;

	public static IO_Boolean slowMotion = new IO_Boolean("SlowMotion",false);

	private boolean slowMotionMode = false;  // Used to track changes

	long cpuStartTime;
	long cpuTimeNow;
	long error, dError, errorOld;

	long targetTime;

	long waitDuration;  //  Initial guess at 
	long maxWait;

	/// Control the error dynamics
	//  kp = 0.7, kd = 0.4, err = +-3ms
	double kp = 0.4;
	double kd = 0.7;  

	final boolean VERBOSE = false;

	/**
	 * @param maxWaitDouble - maximum allowed wait time, in seconds
	 * @param initialTimeDouble - initial simulation time, in seconds
	 */
	public Timer(double maxWaitDouble, double initialTimeDouble) {
		cpuStartTime = System.currentTimeMillis() - (long)(1000*initialTimeDouble);
		maxWait = (long)(1000*maxWaitDouble*SLOW_MOTION_RATE);
		waitDuration = 0;
		errorOld = 0;
	}

	/**
	 * @param initialTimeDouble - initial simulation time, in seconds
	 */
	public void reset(double initialTimeDouble) {
		if (slowMotion.get()){  //  Run in slow motion
			initialTimeDouble = initialTimeDouble * SLOW_MOTION_RATE;
		}
		cpuStartTime = System.currentTimeMillis() - (long)(1000*initialTimeDouble);
		waitDuration = 0;
		errorOld = 0;
	}
	/**
	 * This function is designed to be used inside of a simulation loop. It uses
	 * iterative open-loop control to guess the necessary amount of wait time 
	 * in order to get the simulation time correct.
	 * @param targetTimeDouble (seconds)
	 */
	public void smartPause(double targetTimeDouble){

		if (!slowMotion.get()==slowMotionMode){ // Reset the timer
			slowMotionMode = slowMotion.get();
			reset(targetTimeDouble);
		}

		if (slowMotion.get()){  //  Run in slow motion
			targetTimeDouble = targetTimeDouble * SLOW_MOTION_RATE;
		}

		cpuTimeNow = System.currentTimeMillis() - cpuStartTime;
		targetTime = (long)(1000*targetTimeDouble);
		error = targetTime-cpuTimeNow;
		dError = error - errorOld; errorOld = error;
		
		/// PI Controller:
		waitDuration = waitDuration + (long)(kp*error) + (long)(kd*dError);
		if(waitDuration>maxWait){
			waitDuration = maxWait;
			if (VERBOSE) System.out.println("WARNING - Graphics Timing Error");
		}

		/// Check for timing errors
		if (Math.abs(error) > maxWait){
			reset(targetTimeDouble);
		}
		
		/// Sleeping here:
		if (waitDuration > 0){ // Then tell the cpu to wait for a bit
			try { 
				if(VERBOSE) System.out.println("Timing error: " + error + "ms    Sleeping for :" + waitDuration + " ms");
				Thread.sleep(waitDuration);}
			catch (InterruptedException e) {e.printStackTrace();}
		}

	}
	
	public double getError(){
		return error;
	}

}
