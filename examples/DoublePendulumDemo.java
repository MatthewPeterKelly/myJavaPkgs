package examples;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_dsc.DoublePendulum;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;





//import mpk_gui.Timer;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/** A demonstration for a double pendulum*/
public class DoublePendulumDemo extends JFrame implements ActionListener, KeyListener{

	public double frames_per_second = 30.0;
	private final double buffer_duration = 7.5;  // Seconds

	private DoublePendulum doublependulum;
	private double time;
	private IO_Double rate;
	private int nBuffer = (int) (buffer_duration*frames_per_second);   /// Keep 10 seconds worth of data
	private RingBuffer th;
	private RingBuffer phi;
	private RingBuffer t;
	private ScopePanel scopeAngle;
	private ScopePanel scopeRate;
	private Timer timer;
	private int dt;

	private boolean pause = false;

	public static void main(String[] args) {
		DoublePendulumDemo demo = new DoublePendulumDemo();
		demo.simulate();
	}

	public DoublePendulumDemo() {	

		/// Create a new JFrame and add the two scopes to it
		super("Double Pendulum Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/// Create the pendulum
		doublependulum = new DoublePendulum();

		th = new RingBuffer(nBuffer);
		phi = new RingBuffer(nBuffer);
		t = new RingBuffer(nBuffer);

		th.put(doublependulum.z[0]);
		phi.put(doublependulum.z[1]);
		t.put(time);

		rate = new IO_Double(0.1,1,3,"timeRate");

		scopeAngle = new ScopePanel(t,th);
		scopeAngle.xDataIsMonotonic = true;
		scopeAngle.setAxisExtentsY(-15,15);
		scopeAngle.xLabel = "Time (s)";
		scopeAngle.yLabel = "Angle 1 (rad)";
		scopeAngle.title = "";
		scopeAngle.lineWidth = 2;
		scopeAngle.setSize(300, 450);

		scopeRate = new ScopePanel(t,phi);
		scopeRate.xDataIsMonotonic = true;
		scopeRate.setAxisExtentsY(-15,15);
		scopeRate.xLabel = "Time (s)";
		scopeRate.yLabel = "Angle 2 (rad)";
		scopeRate.title = "";
		scopeRate.lineWidth = 2;
		scopeRate.setSize(300, 450);

		/// Assemble JPanel on left
		JPanel scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(2,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);

		/// Assemble JPanel right
		JPanel graphicsPanel = new JPanel();
		graphicsPanel.setLayout(new GridLayout(2,1));
		graphicsPanel.add(doublependulum.plot);
		graphicsPanel.add(rate.slider);

		/// Assemble JFrame
		setLayout(new GridLayout(1,2));
		add(scopePanel);
		add(graphicsPanel);
		setSize (1200, 800);  //  (width, height)
		setVisible(true);

	}

	public void simulate(){

		/// Animation time!
		time = 0;
		dt = (int) (1000/frames_per_second);

		timer = new Timer(dt,this);
		timer.start();

	}

	public void updatePlots(){
		th.put(doublependulum.z[0]);
		phi.put(doublependulum.z[1]);
		t.put(time);
		scopeAngle.update();
		scopeRate.update();
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		double h = dt*rate.get()/1000.0;

		doublependulum.timeStep(h);
		time += h;

		updatePlots();

		doublependulum.plot.repaint();

	}

	@Override
	public void keyPressed(KeyEvent e) {dispatchKey(e);}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * Handles keyboard events, e.g., spacebar toggles
	 * simulation/pausing, and escape resets the current Task.
	 */
	public void dispatchKey(KeyEvent e)
	{
		System.out.println(KeyEvent.getKeyText(e.getKeyCode()));

		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:  // Toggle simulation pause
			if (pause) {
				timer.start();
			} else {
				timer.stop();
			}
			pause = !pause;
			break;
		case KeyEvent.VK_R:  // Reset the timer rate
			rate.set(1.0);
			break;
		default:
		}

	}

}