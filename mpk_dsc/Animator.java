package mpk_dsc;

import mpk_gui.Timer;

/** A class for running simple animations that have interactive timing features
 * @author matt
 */
public class Animator {

	/** How many times to update the graphics per second */
	public double framesPerSecond = 60;

	/** Time step to pass to the physics solver. */
	public double dt = 0.005;

	private double time;
	private Timer timer;
	private AnimatedSystem sys;
	private double timeRate;

	/** Creates a new animator object */
	public Animator(AnimatedSystem sys){

		this.sys = sys;
		time = 0.0;

		double timeOut = 0.1; // Timer timeout, in seconds
		timer = new Timer(timeOut, 0.0);

	}

	/** Run the animator object */
	public void run(){
		double nStepsDouble;
		double nStepsInt;
		double nStepsErr = 0;
		int nSteps;
		boolean resetTimer = false;
 		while (true) {
			if (!sys.isPaused()) {
				timeRate = sys.getTimeRate();
				
				/// Attempt to minimize error in frame rate
				nStepsDouble = 1/(dt*framesPerSecond) + nStepsErr;
				nStepsInt = Math.floor(nStepsDouble);
				nStepsErr = nStepsDouble - nStepsInt;
				nSteps = (int)(nStepsInt);
				
				/// Run physics
				for (int i=0; i<=nSteps; i++){
					sys.timeStep(dt);
				}
				
				/// Run graphics
				sys.updateGraphics();
				
				/// Timing for the GUI
				time += dt*nSteps/timeRate;
				if (resetTimer){
					timer.reset(time);
					resetTimer = false;
				}
				timer.smartPause(time);	
				
			} else {
				resetTimer = true;
				try {Thread.sleep(10);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}





}
