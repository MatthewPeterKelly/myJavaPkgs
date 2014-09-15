package mpk_dsc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/** A class for running simple animations. An interface is used that allows
 * for simple user interactions, such as pausing the simulation and adjusting 
 * the time rate. The timing will be good if the simulation and graphics calls
 * happen quickly. Otherwise, they will be pushed through the system as fast 
 * as possible.
 * @author matt
 */
public class Animator {

	/** How many times to update the graphics per second */
	private final double framesPerSecond = 30;

	private AnimatedSystem sys;
	private Timer timer;

	/** Creates a new animator object */
	public Animator(AnimatedSystem sys){
		this.sys = sys;
		TimingListener listener = new TimingListener();
		int delay = (int)(1000/framesPerSecond);  //  (milliseconds)
		timer = new Timer(delay,listener);
	}
	
	/** @return frame rate in frames per second */
	public double getFrameRate(){
		return framesPerSecond;
	}

	/** Run the animator object 
	 * @return */
	public void run(){
		timer.start();
	}

	public class TimingListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			sys.updateGraphics(); // Run graphics
			if (!sys.isPaused()){ /// THE IMPORTANT STUFF
				sys.simulate(sys.getTimeRate()/framesPerSecond); // Physics
			}
		}
	}
}
