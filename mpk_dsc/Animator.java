package mpk_dsc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/** A class for running simple animations that have interactive timing features.
 * @author matt
 */
public class Animator {

	/** How many times to update the graphics per second */
	public double framesPerSecond = 30;

	private AnimatedSystem sys;
	private Timer timer;

	/** Creates a new animator object */
	public Animator(AnimatedSystem sys){
		this.sys = sys;
		TimingListener listener = new TimingListener();
		int delay = (int)(1000/framesPerSecond);  //  (milliseconds)
		timer = new Timer(delay,listener);
	}

	/** Run the animator object 
	 * @return */
	public void run(){
		timer.start();
	}

	public class TimingListener implements ActionListener {
		boolean lock = false;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!sys.isPaused()){
				sys.simulate(sys.getTimeRate()/framesPerSecond); // Physics
				sys.updateGraphics(); // Run graphics
			}
		}
	}
}
